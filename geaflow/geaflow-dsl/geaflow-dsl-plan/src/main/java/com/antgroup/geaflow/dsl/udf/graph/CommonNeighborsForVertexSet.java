package com.antgroup.geaflow.dsl.udf.graph;

import com.antgroup.geaflow.common.binary.BinaryString;
import com.antgroup.geaflow.dsl.common.algo.AlgorithmRuntimeContext;
import com.antgroup.geaflow.dsl.common.algo.AlgorithmUserFunction;
import com.antgroup.geaflow.dsl.common.data.Row;
import com.antgroup.geaflow.dsl.common.data.RowEdge;
import com.antgroup.geaflow.dsl.common.data.RowVertex;
import com.antgroup.geaflow.dsl.common.data.impl.ObjectRow;
import com.antgroup.geaflow.dsl.common.function.Description;
import com.antgroup.geaflow.dsl.common.types.GraphSchema;
import com.antgroup.geaflow.dsl.common.types.StructType;
import com.antgroup.geaflow.dsl.common.types.TableField;
import com.antgroup.geaflow.model.graph.edge.EdgeDirection;
import scala.Tuple2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Description(name = "common_neighbors_for_vertex_set", description = "built-in udga for CommonNeighborsForVertexSet")
public class CommonNeighborsForVertexSet implements AlgorithmUserFunction<Object, Tuple2<Boolean, Boolean>> {

    private AlgorithmRuntimeContext<Object, Tuple2<Boolean, Boolean>> context;
    private HashSet<Object> A = new HashSet<>();
    private HashSet<Object> B = new HashSet<>();

    @Override
    public void init(AlgorithmRuntimeContext<Object, Tuple2<Boolean, Boolean>> context, Object[] parameters) {
        this.context = context;
        // 解析参数：第一个参数是A集合，第二个参数是B集合
        Object[] setA = (Object[]) parameters[0];
        Object[] setB = (Object[]) parameters[1];
      
        for (Object id : setA) {
            A.add(id);
        }
        for (Object id : setB) {
            B.add(id);
        }
    }

    @Override
    public void process(RowVertex vertex, Optional<Row> updatedValues, Iterator<Tuple2<Boolean, Boolean>> messages) {
        if (context.getCurrentIterationId() == 1L) {
            // 第一轮：A/B集合顶点发送标识
            boolean isA = A.contains(vertex.getId());
            boolean isB = B.contains(vertex.getId());
          
            if (isA || isB) {
                Tuple2<Boolean, Boolean> messageToSend = new Tuple2<>(isA, isB);
                sendMessageToNeighbors(context.loadEdges(EdgeDirection.BOTH), messageToSend);
            }
          
        } else if (context.getCurrentIterationId() == 2L) {
            // 第二轮：检查共同邻居
            boolean receivedA = false;
            boolean receivedB = false;
          
            while (messages.hasNext()) {
                Tuple2<Boolean, Boolean> msg = messages.next();
                if (msg._1) receivedA = true;
                if (msg._2) receivedB = true;
            }
          
            if (receivedA && receivedB) {
                context.take(ObjectRow.create(vertex.getId()));
            }
        }
    }

    private void sendMessageToNeighbors(List<RowEdge> edges, Tuple2<Boolean, Boolean> message) {
        for (RowEdge edge : edges) {
            context.sendMessage(edge.getTargetId(), message);
        }
    }

    @Override
    public StructType getOutputType(GraphSchema graphSchema) {
        return new StructType(
            new TableField("id", graphSchema.getIdType(), false)
        );
    }
}