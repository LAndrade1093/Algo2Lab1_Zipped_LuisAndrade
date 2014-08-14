import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Iterator;
import edu.neumont.io.*;

public class HuffmanTree 
{
	private Node treeRoot;
	private int messageLength;
	
	private static class Node implements Comparable<Node>
	{
		public byte data;
		public int frequency;
		public Node left;
		public Node right;
		public boolean isLeafNode;
		
		public Node(byte nodeData, int dataFrequency)
		{
			data = nodeData;
			frequency = dataFrequency;
			left = right = null;
		}

		public Node(byte nodeData, int dataFrequency, Node leftNode, Node rightNode)
		{
			data = nodeData;
			frequency = dataFrequency;
			left = leftNode;
			right = rightNode;
		}
		
		@Override
		public int compareTo(Node o) 
		{
			return new Integer(this.frequency).compareTo( new Integer(o.frequency) );
		}
	}
	
	
	public HuffmanTree(byte[] byteArray)
	{
		messageLength = byteArray.length;
		
		HashMap<Byte, Integer> freqDistribution = new HashMap<Byte, Integer>();
		
		for(byte b : byteArray)
		{
			if(freqDistribution.containsKey(b))
			{
				freqDistribution.put(b, freqDistribution.get(b) + 1);
			}
			else
			{
				freqDistribution.put(b, 1);
			}
		}
		
		PriorityQueue<Node> nodeQueue = createDistribution(freqDistribution);
		createFinalTree(nodeQueue);
	}
	
	
	private PriorityQueue<Node> createDistribution(HashMap<Byte, Integer> freqDistribution)
	{
		int initSize = freqDistribution.size();
		PriorityQueue<Node> distribution = new PriorityQueue<Node>(initSize);
		
		Iterator<Byte> keys = freqDistribution.keySet().iterator();
		while(keys.hasNext())
		{
			byte byteData = keys.next();
			int frequency = freqDistribution.get(byteData);
			Node newNode = new Node(byteData, frequency);
			distribution.add(newNode);
		}
		
		return distribution;
	}
	
	private void createFinalTree(PriorityQueue<Node> distribution)
	{
		while(distribution.size() > 1)
		{
			Node leftNode = distribution.poll();
			Node rightNode = distribution.poll();
			int compositeFreq = leftNode.frequency + rightNode.frequency;
			Node compositeNode = new Node((byte)(-1), compositeFreq, leftNode, rightNode);
			distribution.offer(compositeNode);
		}
		treeRoot = distribution.peek();
	}
	
	
	
	public byte toByte(Bits bits) //Read bits
	{
		byte decodedByte = toByteHelper(bits, treeRoot);
		return decodedByte;
	}
	
	private byte toByteHelper(Bits bits, Node currentNode)
	{
		byte currentByte;
		if(bits.size() < 1 || isALeafNode(currentNode))
		{
			currentByte = currentNode.data;
		}
		else
		{
			Boolean bit = bits.poll();
			if(bit == false) //if the binary value is 0
			{
				currentByte = toByteHelper(bits, currentNode.left);
			}
			else //else if the binary value is 1
			{
				currentByte = toByteHelper(bits, currentNode.right);
			}
		}
		return currentByte;
	}
	
	
	
	public void fromByte(byte b, Bits bits) //Write to bits
	{
		Bits fromByteBits = fromByteHelper(b, new Bits(), treeRoot);
		if(fromByteBits != null)
		{
			bits.addAll(fromByteBits);
		}
	}
	
	private Bits fromByteHelper(byte b, Bits newBits, Node currentNode)
	{
		if(isALeafNode(currentNode))
		{
			if(currentNode.data == b)
			{
				return newBits;
			}
			else
			{
				return null;
			}
		}
		else
		{
			int correctBinaryTreePath;
			if( fromByteHelper(b, newBits, currentNode.left) == null ) //Check if left node is valid
			{
				if( fromByteHelper(b, newBits, currentNode.right) == null) //If the left node returns null, check if right node is valid
				{
					return null;
				}
				else
				{
					correctBinaryTreePath = 1; //if the left node returned null but not the right node, go down the path with the binary value of 1;
				}
			}
			else
			{
				correctBinaryTreePath = 0; //if not null, go down the path with the binary value of 0;
			}
			
			Boolean binary = (correctBinaryTreePath == 1) ? true : false;
			newBits.offerFirst(binary);
			
			return newBits;
		}
	}
	
	
	
	private boolean isALeafNode(Node node)
	{
		return (node.left == null && node.right == null);
	}
	
	public int getOriginalMessageLength()
	{
		return messageLength;
	}
}
