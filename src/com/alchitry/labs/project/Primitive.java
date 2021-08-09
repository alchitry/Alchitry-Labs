package com.alchitry.labs.project;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.alchitry.labs.Locations;
import com.alchitry.labs.Named;
import com.alchitry.labs.style.ParseException;

public class Primitive implements Named, Serializable {
	private static final long serialVersionUID = -3360508608633313654L;
	private static final String XILINX_XML = "xilinx.xml";
	private static final String CATEGORY_TAG = "category";
	private static final String PRIMITIVE_TAG = "primitive";
	private static final String PARAM_TAG = "param";
	private static final String OPTION_TAG = "option";
	private static final String RANGE_TAG = "range";
	private static final String MIN_TAG = "min";
	private static final String MAX_TAG = "max";
	private static final String PORT_TAG = "port";

	private static final String NAME_ATTR = "name";
	private static final String TYPE_ATTR = "type";
	private static final String DIRECTION_ATTR = "direction";
	private static final String WIDTH_ATTR = "width";

	private String name;
	private List<Parameter> parameters;
	private List<Port> ports;

	public static HashMap<String, HashSet<Primitive>> getAvailable() throws IOException, ParseException {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(Locations.COMPONENTS + File.separator + XILINX_XML);

		Document document;
		try {
			document = (Document) builder.build(xmlFile);
		} catch (JDOMException e) {
			throw new ParseException(e.getMessage());
		}
		Element library = document.getRootElement();

		HashMap<String, HashSet<Primitive>> fullSet = new HashMap<>();

		List<Element> cat = library.getChildren(CATEGORY_TAG);
		for (Element node : cat) {
			HashSet<Primitive> prims = new HashSet<>();
			if (node.getAttributeValue(NAME_ATTR) == null)
				throw new ParseException("Error while parsing " + XILINX_XML + ": Missing name attribute of category tag.");

			fullSet.put(node.getAttributeValue(NAME_ATTR), prims);

			List<Element> primEntries = node.getChildren(PRIMITIVE_TAG);
			for (Element pNode : primEntries) {
				if (pNode.getAttributeValue(NAME_ATTR) == null)
					throw new ParseException("Error while parsing " + XILINX_XML + ": Missing name of primitive.");
				Primitive prim = new Primitive(pNode.getAttributeValue(NAME_ATTR));

				for (Element param : pNode.getChildren(PARAM_TAG)) {
					if (param.getAttributeValue(NAME_ATTR) == null)
						throw new ParseException("Error while parsing " + XILINX_XML + ": Missing name of parameter.");
					if (param.getAttributeValue(TYPE_ATTR) == null)
						throw new ParseException("Error while parsing " + XILINX_XML + ": Missing type of parameter.");
					Parameter p = new Parameter(param.getAttributeValue(NAME_ATTR));
					if (!p.setType(param.getAttributeValue(TYPE_ATTR)))
						throw new ParseException("Error while parsing " + XILINX_XML + ": Invalid parameter type " + param.getAttributeValue(TYPE_ATTR) + ".");
					if (p.getType().equals(Parameter.TYPE_INTEGER)) {
						for (Element range : param.getChildren(RANGE_TAG)) {
							Element min = range.getChild(MIN_TAG);
							Element max = range.getChild(MAX_TAG);
							int imin = 0, imax = 0;
							if (min != null)
								try {
									imin = Integer.parseInt(min.getValue());
								} catch (NumberFormatException e) {
									throw new ParseException("Error while parsing " + XILINX_XML + ": Min value not an interger.");
								}
							if (max != null)
								try {
									imax = Integer.parseInt(max.getValue());
								} catch (NumberFormatException e) {
									throw new ParseException("Error while parsing " + XILINX_XML + ": Max value not an interger.");
								}

							p.addRange(imin, imax);
						}
					} else if (p.getType().equals(Parameter.TYPE_REAL)) {
						for (Element range : param.getChildren(RANGE_TAG)) {
							Element min = range.getChild(MIN_TAG);
							Element max = range.getChild(MAX_TAG);
							double imin = 0, imax = 0;
							if (min != null)
								try {
									imin = Double.parseDouble(min.getValue());
								} catch (NumberFormatException e) {
									throw new ParseException("Error while parsing " + XILINX_XML + ": Min value not a double.");
								}
							if (max != null)
								try {
									imax = Double.parseDouble(max.getValue());
								} catch (NumberFormatException e) {
									throw new ParseException("Error while parsing " + XILINX_XML + ": Max value not a double.");
								}

							p.addRange(imin, imax);
						}
					} else { // string
						for (Element option : param.getChildren(OPTION_TAG)) {
							p.addOption(option.getValue());
						}
					}
					prim.addParameter(p);
				}

				for (Element port : pNode.getChildren(PORT_TAG)) {
					String name = port.getAttributeValue(NAME_ATTR);
					String dir = port.getAttributeValue(DIRECTION_ATTR);
					String width = port.getAttributeValue(WIDTH_ATTR);
					if (name == null)
						throw new ParseException("Error while parsing " + XILINX_XML + ": Missing name of port.");
					if (dir == null)
						throw new ParseException("Error while parsing " + XILINX_XML + ": Missing direction of port.");
					if (width == null)
						throw new ParseException("Error while parsing " + XILINX_XML + ": Missing width of port.");
					int w;
					try {
						w = Integer.parseInt(width);
					} catch (NumberFormatException e) {
						throw new ParseException("Error while parsing " + XILINX_XML + ": Width value not an interger.");
					}
					Port p = new Port(name);
					p.setWidth(w);
					if (!p.setDirection(dir))
						throw new ParseException("Error while parsing " + XILINX_XML + ": Invalid port direction.");
					prim.addPort(p);
				}
				prims.add(prim);
			}

		}
		return fullSet;

	}

	public Primitive(String name) {
		this.name = name;
		parameters = new ArrayList<>();
		ports = new ArrayList<>();
	}

	public void addParameter(Parameter p) {
		parameters.add(p);
	}

	public void addPort(Port p) {
		ports.add(p);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public List<Port> getPorts() {
		return ports;
	}

	@Override
	public String toString() {
		return name + " #(" + parameters.toString() + ")(" + ports.toString() + ")";
	}

	@Override
	public String getName() {
		return name;
	}

	public static class Port implements Named {
		public static final String DIR_INPUT = "input";
		public static final String DIR_OUTPUT = "output";
		public static final String DIR_INOUT = "inout";

		private String name;
		private String direction;
		private int width;

		public Port(String name) {
			this.name = name;
		}

		public boolean setDirection(String dir) {
			switch (dir) {
			case DIR_INOUT:
			case DIR_INPUT:
			case DIR_OUTPUT:
				direction = dir;
				return true;
			default:
				return false;
			}
		}

		public void setWidth(int w) {
			width = w;
		}

		public String getDirection() {
			return direction;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(direction).append(" ").append(name);
			sb.append("[").append(width).append("]");
			return sb.toString();
		}

		@Override
		public String getName() {
			return name;
		}

		public int getWidth() {
			return width;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Port) {
				Port p = (Port) o;
				if (Objects.equals(name, p.name)) {
					if (Objects.equals(direction, p.direction)) {
						if (width == p.width)
							return true;
					}
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = width;
			if (name != null)
				hash ^= name.hashCode();
			if (direction != null)
				hash ^= direction.hashCode();
			return hash;
		}
	}

	public static class Parameter implements Named, Serializable {
		private static final long serialVersionUID = -782963467721844260L;
		public static final String TYPE_STRING = "string";
		public static final String TYPE_INTEGER = "integer";
		public static final String TYPE_REAL = "real";

		private String name;
		private List<String> options;
		private List<Range<?>> ranges;
		private String type;

		public static class Range<T extends Number & Comparable<T>> implements Serializable{
			private static final long serialVersionUID = 6482276553489423635L;
			public T min, max;

			public Range(T min, T max) {
				this.min = min;
				this.max = max;
			}

			public boolean inRange(Comparable<T> i) {
				return i.compareTo(min) >= 0 && i.compareTo(max) <= 0;
			}

			@Override
			public int hashCode() {
				return min.hashCode() ^ max.hashCode();
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Range)
					if (((Range<?>) o).min.equals(min) && ((Range<?>) o).max.equals(max))
						return true;
				return false;
			}

			@Override
			public String toString() {
				return min + ":" + max;
			}
		}

		public Parameter(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name).append(": ");
			if (type.equals(TYPE_STRING)) {
				sb.append(options.toString());
			} else {
				for (Range<?> r : ranges)
					sb.append("[").append(r).append("]");
			}
			return sb.toString();
		}

		@Override
		public String getName() {
			return name;
		}

		public boolean setType(String type) {
			switch (type) {
			case TYPE_STRING:
				options = new ArrayList<>();
				break;
			case TYPE_INTEGER:
			case TYPE_REAL:
				ranges = new ArrayList<>();
				break;
			default:
				return false;
			}
			this.type = type;
			return true;
		}

		public String getType() {
			return type;
		}

		public void addOption(String option) {
			options.add(option);
		}

		public void addRange(int min, int max) {
			ranges.add(new Range<Integer>(min, max));
		}
		
		public void addRange(double min, double max) {
			ranges.add(new Range<Double>(min, max));
		}

		@SuppressWarnings("unchecked")
		public boolean inRange(int i) {
			if (type.equals(TYPE_INTEGER)) {
				for (Range<?> r : ranges)
					if (((Range<Integer>) r).inRange(Integer.valueOf(i)))
						return true;
			} else if (type.equals(TYPE_REAL))
				for (Range<?> r : ranges)
					if (((Range<Double>) r).inRange(Double.valueOf(i)))
						return true;
			return false;
		}

		@SuppressWarnings("unchecked")
		public boolean inRange(double d) {
			if (type.equals(TYPE_INTEGER)) {
				throw new InvalidParameterException("inRange(double d) can only be used with \"real\" type parameters");
			} else if (type.equals(TYPE_REAL))
				for (Range<?> r : ranges)
					if (((Range<Double>) r).inRange(Double.valueOf(d)))
						return true;
			return false;
		}

		public boolean isValidOption(String option) {
			return options.contains(option);
		}

		public List<String> getOptions() {
			return options;
		}

		public List<Range<?>> getRanges() {
			return ranges;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Parameter) {
				Parameter p = (Parameter) o;
				if (Objects.equals(name, p.name))
					if (Objects.equals(type, p.type))
						if (Objects.equals(options, p.options))
							if (Objects.equals(ranges, p.ranges))
								return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = ranges.hashCode();
			if (name != null)
				hash ^= name.hashCode();
			if (options != null)
				hash ^= options.hashCode();
			if (type != null)
				hash ^= type.hashCode();
			return hash;
		}
	}
}
