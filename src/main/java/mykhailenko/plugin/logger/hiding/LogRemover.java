package mykhailenko.plugin.logger.hiding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;

public class LogRemover {

	private final ConfigurationBean bean;

	public LogRemover(ConfigurationBean bean) {
		this.bean = bean;
	}

	public void removeUnnessaceryLogs(File classFile) throws IOException {
		ClassParser classParser = new ClassParser(classFile.getAbsolutePath());
		JavaClass clazz = classParser.parse();
		Method[] methods = clazz.getMethods();
		ConstantPoolGen cpg = new ConstantPoolGen(clazz.getConstantPool());
		for (int i = 0; i < methods.length; i++) {
			if (!(methods[i].isAbstract() || methods[i].isNative())) {
				MethodGen mg = new MethodGen(methods[i], clazz.getClassName(),
						cpg);
				methods[i] = removeLogs(mg, bean, cpg);
			}
		}
		clazz.setConstantPool(cpg.getFinalConstantPool());
		OutputStream fos = new FileOutputStream(classFile);
		clazz.dump(fos);
	}

	private Method removeLogs(MethodGen mg, ConfigurationBean bean,
			ConstantPoolGen cpg) {
		InstructionList il = mg.getInstructionList();
		for (InstructionHandle instructionHandle : il.getInstructionHandles()) {
			Instruction instruction = instructionHandle.getInstruction();
			if (instruction instanceof InvokeInstruction) {
				InvokeInstruction invoke = (InvokeInstruction) instruction;
				String className = invoke.getClassName(cpg);
				String methodName = invoke.getMethodName(cpg);
				if (matchClassName(className, bean)
						&& matchMethodName(methodName, bean)) {
					try {
						il.delete(instructionHandle);
					} catch (TargetLostException e) {
					}
				}
			}
		}
		Method m = mg.getMethod();
		il.dispose();
		return m;
	}

	private boolean matchMethodName(String methodName, ConfigurationBean bean) {
		DeleteLoggsMojo.Level methodLevel = getMethodLevelIfItLogerMethod(methodName);
		return isMethodLevelLowerThanCurrentLevel(methodLevel,
				bean.getCurrentLoggingLevel());
	}

	private boolean isMethodLevelLowerThanCurrentLevel(
			DeleteLoggsMojo.Level methodLevel,
			DeleteLoggsMojo.Level currentLevel) {
		return methodLevel != null
				&& methodLevel.compareTo(bean.getCurrentLoggingLevel()) > 0;
	}

	private DeleteLoggsMojo.Level getMethodLevelIfItLogerMethod(
			String methodName) {
		for (DeleteLoggsMojo.Level level : DeleteLoggsMojo.Level.values()) {
			if (level.toString().toLowerCase().equals(methodName)) {
				return level;
			}
		}
		return null;
	}

	private boolean matchClassName(String className, ConfigurationBean bean) {
		for (String loggerClazzName : bean.getLoggerClasses()) {
			if (loggerClazzName.equals(className)) {
				return true;
			}
		}
		return false;
	}

}
