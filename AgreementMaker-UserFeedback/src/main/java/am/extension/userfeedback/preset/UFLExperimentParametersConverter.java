package am.extension.userfeedback.preset;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import am.extension.userfeedback.experiments.UFLExperimentParameters;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class UFLExperimentParametersConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(UFLExperimentParameters.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		UFLExperimentParameters p = (UFLExperimentParameters) source;

		List<String> keyList = new LinkedList<>();
		for( Object key : p.keySet() ) {
			keyList.add((String)key);
		}
		
		Collections.sort(keyList);
		
		for( String key : keyList ) {
			Parameter param = Parameter.getParameter(key);
			String value = p.getParameter(param);
			writer.startNode(param.name());
			writer.setValue(value);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		UFLExperimentParameters p = new UFLExperimentParameters();
		
		final String n = reader.getNodeName();
		final String v = reader.getValue();
		
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getNodeName();
			String value = reader.getValue();
			reader.moveUp();
			p.setParameter(Parameter.getParameter(key), value);
		}
		
		return p;
	}

}
