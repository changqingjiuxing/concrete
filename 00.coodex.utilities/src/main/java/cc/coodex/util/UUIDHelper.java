/**
 * 
 */
package cc.coodex.util;

import java.util.UUID;

/**
 * @author davidoff
 *
 */
public class UUIDHelper {

   private static String digits(long val, int digits) {
      long hi = 1L << (digits * 4);
      return Long.toHexString(hi | (val & (hi - 1))).substring(1);
   }

   public static byte[] getUUIDBytes(){
      UUID uuid = UUID.randomUUID();
      long longOne = uuid.getMostSignificantBits();
      long longTwo = uuid.getLeastSignificantBits();

      return new byte[] {
              (byte)(longOne >>> 56),
              (byte)(longOne >>> 48),
              (byte)(longOne >>> 40),
              (byte)(longOne >>> 32),
              (byte)(longOne >>> 24),
              (byte)(longOne >>> 16),
              (byte)(longOne >>> 8),
              (byte) longOne,
              (byte)(longTwo >>> 56),
              (byte)(longTwo >>> 48),
              (byte)(longTwo >>> 40),
              (byte)(longTwo >>> 32),
              (byte)(longTwo >>> 24),
              (byte)(longTwo >>> 16),
              (byte)(longTwo >>> 8),
              (byte) longTwo
      };
   }

   public static String getUUIDString() {
      UUID uuid = UUID.randomUUID();
      long mostSigBits = uuid.getMostSignificantBits();
      long leastSigBits = uuid.getLeastSignificantBits();
      return (digits(mostSigBits >> 32, 8) + digits(mostSigBits >> 16, 4)
            + digits(mostSigBits, 4) + digits(leastSigBits >> 48, 4) + digits(
               leastSigBits, 12));
   }

   public static String getUUIDStringWithBase58(){
      return Base58.encode(getUUIDBytes());
   }


   public static void main(String [] args){
      for(int i = 0; i < 1000; i ++)
         System.out.println(getUUIDStringWithBase58());
   }

}
