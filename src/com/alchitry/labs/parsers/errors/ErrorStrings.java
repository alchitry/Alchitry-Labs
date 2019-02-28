package com.alchitry.labs.parsers.errors;

import com.alchitry.labs.parsers.lucid.Lucid;

public class ErrorStrings {
	public static final String UNDECLARED_CONST = "This constant was not declared";
	public static final String UNDECLARED_NAME = "The name \"%s\" could not be resolved";
	public static final String UNDECLARED_MODULE = "The module \"%s\" could not be found";

	public static final String PARAMETER_CONSTRAINT_FAILED = "The constraint \"%s\" for parameter \"%s\" with value \"%s\" failed";
	public static final String PARAMETER_CONSTRAINT_PARSE_FAIL = "The constraint \"%s\" could not be parsed";
	
	public static final String NUMBER_PARSE_FAIL = "The value \"%s\" could not be parsed";

	public static final String DFF_MISSING_CLK = "The \"clk\" input to the DFF must be assigned";
	public static final String DFF_NO_SIG_SELECTED = "DFFs can not be accessed directly. You must select a signal with \".d\" or \".q\"";
	public static final String DFF_INVALID_SIG = "The signal \"%s\" is not a valid DFF signal. Only \"d\" and \"q\" are valid signals";
	public static final String DFF_Q_ASSIGNED = "You can not assign a value to the \"q\" output of a DFF. You should assign values to the \"d\" input";
	public static final String DFF_D_READ = "The \"d\" input of a DFF can not be read. You should read the \"q\" output instead";
	public static final String DFF_UNKNOWN_INPUT = "The input \"%s\" does not belong to DFFs";
	public static final String DFF_UNKNOWN_PARAM = "The parameter \"%s\" does not belong to DFFs";

	public static final String FSM_MISSING_CLK = "The \"clk\" input to the FSM must be assigned";
	public static final String FSM_NO_SIG_SELECTED = "FSMs can not be accessed directly. You must select a signal with \".d\" or \".q\"";
	public static final String FSM_INVALID_SIG = "The signal \"%s\" is not a valid FSM signal. Only \"d\" and \"q\" are valid signals";
	public static final String FSM_Q_ASSIGNED = "You can not assign a value to the \"q\" output of an FSM. You should assign values to the \"d\" input";
	public static final String FSM_D_READ = "The \"d\" input of an FSM can not be read. You should read the \"q\" output instead";
	public static final String FSM_UNKNOWN_INPUT = "The input \"%s\" does not belong to FSMs";
	public static final String FSM_UNKNOWN_PARAM = "The parameter \"%s\" does not belong to FSMs";
	public static final String FSM_INVALID_STATE = "The state \"%s\" does not belong to the FSM \"%s\"";
	public static final String FSM_INVALID_STATE_NAME = "\"%s\" cannot be used as a state name";

	public static final String MISSPELLED = "Did you mean \"%s\"?";

	public static final String INOUT_CONNECTION_INDIRECT = "Inouts can only be accessed directly in port connections";
	public static final String INOUT_ACCESSED_DIRECTLY = "Inout signals cannot be accessed directly";
	public static final String INOUT_UNKNOWN_SIG = "\"%s\" is not part of inout signals";
	public static final String SIG_NOT_MODULE = "\"%s\" is a signal and has no sub-signals.";
	public static final String VAR_NOT_MODULE = "\"%s\" is a variable so the \".\" operator cannot be used on it.";

	public static final String NAME_TAKEN = "The name \"%s\" is already in use";

	public static final String SIG_UNDEFINED = "The signal \"%s\" does not belong to the signal \"%s\"";

	public static final String MODULE_NO_SIG_SELECTED = "Modules can not be accessed directly. You must select a signal with \".signame\" where \"signame\" is the name of an input or output of the module";
	public static final String MODULE_SIG_UNDEFINED = "The signal \"%s\" does not belong to the module type \"%s\"";
	public static final String MODULE_UNKNOWN_PARAM = "The parameter \"%s\" does not belong to the module \"%s\"";
	public static final String MODULE_UNKNOWN_INPUT = "The input \"%s\" does not belong to the module \"%s\"";
	public static final String MODULE_PARAM_DEFINED = "The parameter \"%s\" was already defined";
	public static final String MODULE_INPUT_DEFINED = "The input \"%s\" was already defined";
	public static final String MODULE_INOUT_DEFINED = "The inout \"%s\" was already defined";
	public static final String MODULE_INOUT_CONNECT_ONLY_INOUT = "Inouts can only be directly connected to another inout";
	public static final String MODULE_MISSING_REQ_PARAM = "The required parameter \"%s\" was not specified";
	public static final String MODULE_MISSING_REQ_PARAMS = "The required parameters \"%s\" were not specified";
	public static final String MODULE_DIM_PARSE_FALIED = "Could not determine the dimensions of %s";
	public static final String MODULE_IO_SIZE_NAN = "The signal \"%s\" dimensions must be a number";
	public static final String MODULE_SIG_ALREADY_ASSIGNED = "The signal \"%s\" has already been assigned";
	public static final String MODULE_INPUT_NOT_ASSIGNED = "The signal \"%s\" was not assigned";
	public static final String MODULE_INPUTS_NOT_ASSIGNED = "The signals \"%s\" were not assigned";

	public static final String ARRAY_SIZE_MULTI_DIM = "Array dimension sizes must be one dimensional";
	public static final String ARRAY_SIZE_NAN = "Array dimension sizes must be a number (no x or z values)";
	public static final String ARRAY_SIZE_NEG = "Array size must be positive";
	public static final String ARRAY_SIZE_TOO_BIG = "Array dimension is HUGE, no error checking can be performed";
	public static final String ARRAY_INDEX_MULTI_DIM = "Array dimensions must be one dimensional";
	public static final String ARRAY_CONCAT_DIM_MISMATCH = "Each element in an array concatenation must have the same dimensions";
	public static final String ARRAY_DUP_INDEX_MULTI_DIM = "The array duplication index must be one dimensional";
	public static final String ARRAY_DUP_INDEX_NAN = "The array duplication index must be a number (no x or z values)";
	public static final String ARRAY_BUILDING_DIM_MISMATCH = "Each element in an array builder must have the same dimensions";
	public static final String ARRAY_INDEX_OUT_OF_BOUNDS = "This index is out of bounds";
	public static final String ARRAY_INDEX_DIM_MISMATCH = "There are too many indices for this array";
	public static final String ARRAY_INDEX_NAN = "Array indices must be a number (no x or z values)";
	public static final String ARRAY_INDEX_TOO_BIG = "Array index is HUGE, no error checking can be performed";

	public static final String BIT_SELECTOR_ARRAY = "The value used in bit selection can't be an array";
	public static final String BIT_SELECTOR_ORDER = "The value on the left must be greater or equal to the value on the right";
	public static final String BIT_SELECTOR_NAN = "Bit selectors must be a number (no x or z values)";
	public static final String BIT_SELECTOR_IN_NAME = "This bit selector should not be here";
	public static final String EXTRA_BIT_SELECTORS = "FSMs can only have one bit selector";

	public static final String NEG_MULTI_DIM = "Only single dimensional arrays can be negated";
	public static final String MUL_MULTI_DIM = "Only single dimensional arrays can be multiplied";
	public static final String DIV_MULTI_DIM = "Only single dimensional arrays can be divided";
	public static final String ADD_MULTI_DIM = "Only single dimensional arrays can be added";
	public static final String SUB_MULTI_DIM = "Only single dimensional arrays can be subtracted";
	public static final String SHIFT_MULTI_DIM = "Only single dimensional arrays can be shifted";

	public static final String OR_MULTI_DIM_MISMATCH = "When performing an OR on multi-dimensional arrays their dimensions must match";
	public static final String AND_MULTI_DIM_MISMATCH = "When performing an AND on multi-dimensional arrays their dimensions must match";

	public static final String VALUE_TOO_BIG = "The value \"%s\" is wider than %s bits and it will be truncated";

	public static final String OP_GT_ARRAY = "The greater than operator \">\" can't be used on arrays";
	public static final String OP_LT_ARRAY = "The less than operator \"<\" can't be used on arrays";
	public static final String OP_GTE_ARRAY = "The greater than or equal operator \">=\" can't be used on arrays";
	public static final String OP_LTE_ARRAY = "The less than or equal operator \"<=\" can't be used on arrays";
	public static final String OP_EQ_DIM_MISMATCH = "The dimensions of both arrays must match for the equal operator";
	public static final String OP_NEQ_DIM_MISMATCH = "The dimensions of both arrays must match for the not equal operator";
	public static final String OP_TERN_DIM_MISMATCH = "The dimensions of both results must match for the ternary operator";

	public static final String PORT_DIM_MISMATCH = "The signal \"%s\" does not match the dimensions of the port \"%s\"";

	public static final String ASSIGN_ARRAY_DIM_MISMATCH = "When assigning two arrays, their dimensions must match";
	public static final String ASSIGN_SIG_NOT_ARRAY = "When assigning two arrays, their dimensions must match";
	public static final String TRUNC_WARN = "The signal \"%s\" is wider than \"%s\" and the most significant bits will be dropped";

	public static final String NUM_WIDTH_NAN = "The width of a value must be a number (no x or z values)";

	public static final String SIG_READ_ONLY = "The signal \"%s\" is read only and can not be written";
	public static final String SIG_WRITE_ONLY = "The signal \"%s\" is write only and can not be read";
	public static final String READ_BEFORE_WRITE = "The signal \"%s\" must be written before it can be read in this always block";
	public static final String MULTIPLE_DRIVERS = "The signal \"%s\" has already been assigned";

	public static final String OUPUT_NEVER_ASSIGNED = "The output \"%s\" was never assigned";
	public static final String NEVER_USED = "\"%s\" was never used";

	public static final String INVALID_ATTRIBUTE = "\"%s\" is not a valid attribute. Only \"" + Lucid.WIDTH_ATTR + "\" is";
	public static final String ATTRIBUTE_READ_ONLY = "Attributes are read only";
	public static final String ATTRIBUTE_BIT_SELECT = "Bit selection can only be used on the \"" + Lucid.WIDTH_ATTR + "\" attribute";

	public static final String UNKNOWN_FUNCTION = "The function \"%s\" is unknown";
	public static final String CONST_FUNCTION = "The function \"%s\" can only be used on constants";
	public static final String FUNCTION_ARG_NAN = "The argument \"%s\" with value \"%s\" must be a number";
	public static final String FUNCTION_ARG_COUNT = "The function \"%s\" takes exactly %d argument(s)";
	public static final String FUNCTION_NO_ARRAY = "The function \"%s\" can't be used on multidimensional arrays";
	public static final String FUNCTION_ARG_ZERO = "The argument \"%s\" can't be zero";

	public static final String EXPR_NOT_CONSTANT = "The expression \"%s\" must be constant";
	public static final String NAME_NOT_CONST = "The name \"%s\" is not a constant name. Constants can only consist of capital letters and underscores";
	public static final String NAME_NOT_TYPE = "The name \"%s\" is not a type name. It must start with a lowercase letter";
	public static final String CONST_NO_MEMBERS = "Constant \"%s\" doesn't have member \"%s\"";
	public static final String CONST_READ_ONLY = "Constants can only be read";
	public static final String BIT_SELECT_WIDTH = "Bit selection can not be used on the constant WIDTH";
	public static final String SIG_AFTER_BIT_SELECT = "The only signal or attribute that can come after a bit selection is WIDTH";
	
	public static final String STRING_CANNOT_BE_EMPTY = "String constants cannot be empty.";
	public static final String MULTIPLE_MODULES = "You can only have one module declaration per file";
	public static final String UNKNOWN_STRUCT_NAME = "The member \"%s\" does not belong to struct \"%s\"";
	public static final String UNKNOWN_STRUCT = "\"%s\" is not a known struct";
	public static final String LOCAL_STRUCT_ONLY = "Only local structs can be accessed in a global block";
	
	public static final String ADD_SUB_NOT_ARRAY = "Addition and subtraction can't be performed on structs";
	public static final String SHIFT_NOT_ARRAY = "Shifts can't be performed on structs";
	public static final String BIT_SELECTOR_STRUCT = "Bit selectors can't be structs";
	public static final String ARRAY_INDEX_STRUCT = "Array indices can't be structs";
	public static final String ARRAY_CONCAT_STRUCT = "Concatenation can't be performed on structs";
	public static final String ARRAY_DUP_STRUCT = "Duplication can't be performed on structs";
	public static final String MUL_DIV_STRUCT = "Multiplication and division can't be performed on structs";
	public static final String NAMESPACE_CASE = "The name of a global block must begin with a capital and contain at least one lowercase letter";
	public static final String NAMESPACE_IN_USE = "The name \"%s\" is already in use";
	public static final String STRUCT_MEMBER_CASE = "Struct members must start with a lowercase letter";
	public static final String STRUCT_NAME_CASE = "A struct's name must start with a lowercase letter";
	public static final String NAMESPACE_DIRECT = "Global namespaces need to have a constant or struct selected";
	public static final String UNKNOWN_NAMESPACE = "\"%s\" is not a known global namespace";
	public static final String NOT_IN_NAMESPACE = "\"%s\" is no in the global namespace \"%s\"";
	public static final String NOT_A_MEMBER = "\"%s\" is not a member of \"%s\"";
	public static final String STRUCT_NOT_ARRAY = "Bit selectors can only be used on arrays, not structs";
	public static final String INOUT_NOT_ARRAY = "Bit selectors can only be used on arrays, not inouts (select an inout member first)";
	public static final String MODULE_IO_MISSING = "The inout(s), %s, were not connected. Inouts can only be passed out of the module";
	public static final String WIDTH_NOT_SIMPLE_ARRAY = Lucid.WIDTH_ATTR +" can only be used on arrays that do not contain structs";
	public static final String WIDTH_COULD_NOT_BE_EVALUATED = Lucid.WIDTH_ATTR + " can only be indexed with constants (it must be evaluated during synthesis)";
	
	public static final String PRIMITIVE_INVALID_OPTION = "The value %s is not a valid option. Use one of the following: %s";
	public static final String PRIMITIVE_NAI = "The parameter \"%s\" only accepts integers";
	public static final String PRIMITIVE_NAD = "The parameter \"%s\" only accepts real numbers";
	public static final String PRIMITIVE_OUT_OF_RANGE = "%s must be in the range %s";
	
	public static final String CONSTRAINT_PORT_UNKNOWN = "The port %s is not a port of the top module";
	public static final String CONSTRAINT_UNSUPPORTED_ARRAY = "The port %s is of an unsupported type";
	public static final String CONSTRAINT_UP_AND_DOWN = "Only pullup or pulldown can be specified for a single pin";
	public static final String CONSTRAINT_UNKNOWN_UNIT = "Unknown frequency unit \"%s\"";
	
}
