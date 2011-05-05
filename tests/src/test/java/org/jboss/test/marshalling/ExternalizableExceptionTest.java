package org.jboss.test.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;
import org.jboss.marshalling.reflect.SunReflectiveCreator;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * // TODO: Document this
 *
 * @author Galder Zamarreño
 * @since // TODO
 */
@Test
public class ExternalizableExceptionTest {

   public void test000() throws Exception {
      MarshallingConfiguration cfg = new MarshallingConfiguration();
      MarshallerFactory factory = Marshalling.getMarshallerFactory("river", Marshalling.class.getClassLoader());

      PojoWhichFailsOnUnmarshalling pojo = new PojoWhichFailsOnUnmarshalling();
      Marshaller marshaller = factory.createMarshaller(cfg);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      marshaller.start(Marshalling.createByteOutput(os));
      try {
         marshaller.writeObject(pojo);
      } finally {
         marshaller.finish();
      }

      Unmarshaller unmarshaller = factory.createUnmarshaller(cfg);
      ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
      unmarshaller.start(Marshalling.createByteInput(is));
      try {
         unmarshaller.readObject();
      } finally {
         unmarshaller.finish();
      }
   }

   static class PojoWhichFailsOnUnmarshalling extends Pojo {
      private static final long serialVersionUID = -5109779096242560884L;

      @Override
      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
         throw new IOException("Injected failue!");
      }

   }

   static class Pojo implements Externalizable {
      int i;
      boolean b;
      static int serializationCount, deserializationCount;
      private static final long serialVersionUID = 9032309454840083326L;

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }

         Pojo pojo = (Pojo) o;

         if (b != pojo.b) {
            return false;
         }
         if (i != pojo.i) {
            return false;
         }

         return true;
      }

      public int hashCode() {
         int result;
         result = i;
         result = 31 * result + (b ? 1 : 0);
         return result;
      }

      public void writeExternal(ObjectOutput out) throws IOException {
         out.writeInt(i);
         out.writeBoolean(b);
         serializationCount++;
      }

      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
         i = in.readInt();
         b = in.readBoolean();
         deserializationCount++;
      }
   }

}
