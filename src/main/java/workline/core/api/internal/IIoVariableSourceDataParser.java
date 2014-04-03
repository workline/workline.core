package workline.core.api.internal;

import java.util.List;
import java.util.Set;

import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.domain.ProcessElementVariableMappingDefinition;

public interface IIoVariableSourceDataParser {

    List<ProcessElementVariableMappingDefinition> parseIoVariableSourceData(Set<String> ioFlatDataSet);

}
