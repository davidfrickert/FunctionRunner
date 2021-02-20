package pt.ist.photon_graal.isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class BooleanConverter implements TypeConverter<Boolean>  {
	@Override
	public ObjectHandle createHandle (IsolateThread targetIsolate, Boolean b) {
		byte sadfsd = CTypeConversion.toCBoolean(b);
		return toJava(targetIsolate, sadfsd);
	}

	@CEntryPoint
	private static ObjectHandle toJava(@CEntryPoint.IsolateThreadContext IsolateThread targetIsolate, byte bool) {
		return ObjectHandles.getGlobal().create(CTypeConversion.toBoolean(bool));
	}
}
