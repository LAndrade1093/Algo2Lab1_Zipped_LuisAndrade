import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import edu.neumont.io.Bits;


public class HuffmanCompressor 
{
	HashMap<Byte, Bits> encodedBytes = new HashMap<>();
	private int bitPaddingCount;
	
	public byte[] compress(HuffmanTree tree, byte[] b) //fromByte
	{
		bitPaddingCount = 0;
		Bits compressedBits = new Bits();
		for(int i = 0; i < b.length; i++)
		{
			if(encodedBytes.containsKey(b[i]))
			{
				compressedBits.addAll(encodedBytes.get(b[i]));
			}
			else
			{
				Bits code = new Bits();
				tree.fromByte(b[i], code);
				encodedBytes.put(b[i], code);
				compressedBits.addAll(code);
			}
		}
		
		while(compressedBits.size() % 8 != 0) //Padding at the end, if needed
		{
			compressedBits.offerLast(false); //Add a 0 for padding
			bitPaddingCount++;
		}
		
		String binaryData = "";
		int compressedBitsSize = compressedBits.size();
		for(int i = 0; i < compressedBitsSize; i++)
		{
			Boolean binaryIsTrue = compressedBits.poll().booleanValue();
			binaryData += (binaryIsTrue) ? "1" : "0";
		}
		
		byte[] compressedByte = new BigInteger(binaryData, 2).toByteArray();
		
		return compressedByte;
	}
	
	
	public byte[] decompress(HuffmanTree tree, int uncompressedLength, byte[] b)
	{
		BigInteger byteData = new BigInteger(b);
		String binaryData = byteData.toString(2);
		
		Bits decompressedBits = new Bits();
		int binaryStringLength = binaryData.length();
		for(int i = 0; i < binaryStringLength; i++)
		{
			int bit = Integer.parseInt(binaryData.substring(i, i+1));
			boolean binary = (bit == 1) ? true : false;
			decompressedBits.offerLast(binary);
		}
		
		byte[] decompressedBytes = new byte[uncompressedLength];
		for(int i = 0; i < uncompressedLength; i++)
		{
			decompressedBytes[i] = tree.toByte(decompressedBits);
		}
		
		return decompressedBytes;
	}
	
	
	public int getBitPaddingCount()
	{
		return bitPaddingCount;
	}

}
