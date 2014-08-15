import java.math.BigInteger;
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
		
		int compressedBitsSize = compressedBits.size();
		byte[] compressedByte = new byte[compressedBitsSize/8];
		for(int i = 0; i < compressedBitsSize/8; i++)
		{
			for(int j = 7; j >= 0; j--)
			{
				boolean binaryIsTrue = compressedBits.poll();
				if(binaryIsTrue)
				{
					compressedByte[i] += 1 << j;
				}
			}
		}
		
		return compressedByte;
	}
	
	
	public byte[] decompress(HuffmanTree tree, int uncompressedLength, byte[] b)
	{
	    Bits decompressedBits = new Bits();
		for(int i = 0; i < b.length; i++)
		{
			for(int j = 7; j >= 0; j--)
			{
				if(((b[i] >> j) & 1) == 1)
				{
					decompressedBits.offerLast(true);
				}
				else
				{
					decompressedBits.offerLast(false);
				}
			}
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