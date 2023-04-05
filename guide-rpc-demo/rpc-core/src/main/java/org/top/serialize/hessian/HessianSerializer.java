package org.top.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.top.exception.SerializeException;
import org.top.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            HessianOutput hessianOutput = new HessianOutput(out);
            hessianOutput.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e){
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream in = new ByteArrayInputStream(bytes)){
            HessianInput hessianInput = new HessianInput(in);
            Object object = hessianInput.readObject();
            return clazz.cast(object);
        } catch (IOException e){
            throw new SerializeException("Deserialization failed");
        }
    }
}
