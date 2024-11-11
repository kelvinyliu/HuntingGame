package KelvinList;

import java.util.Iterator;

public class KelvinList<T> implements Iterable<T> {
    private Node firstObject;
    private int size;
    private Node lastObject;

    private Node get(int index, Node currentNode)
    {

        if (index == 0)
        {
            return currentNode;
        } else
        {
            return get(index -1, currentNode.nextNode);
        }
    }

    public Node retrieve(int index)
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Cannot retrieve element from index " + index);

        return get(index, firstObject);
    }

    public void remove(int index)
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Cannot retrieve element from index " + index);
        
        if (index == 0)
        {
            if (firstObject == lastObject)
            {
                firstObject = null;
                lastObject = null;
            } else // Removing the first element in a list of size > 1
            {
                firstObject = firstObject.nextNode;
            }
        }  else
        {
            Node previousObject = get(index - 1, firstObject);
            Node currentObject = previousObject.nextNode;

            if (currentObject == lastObject)
            {
                lastObject = previousObject;
                previousObject.nextNode = null;
            } else
            {
                previousObject.nextNode = currentObject.nextNode;
            }
        }
        size--;
    }
    
    
    public int getSize()
    {
        return size;
    }

    public void append(T data)
    {
        if (data == null)
            throw new NullPointerException("Cannot append a null value");
        Node newNodeItem = new Node();
        newNodeItem.data = data;

        if (firstObject == null)
        {
            firstObject = newNodeItem;
        } else
        {
            lastObject.nextNode = newNodeItem;
        }
        lastObject = newNodeItem;
        size++;
    }
    

    public KelvinList(T... args)
    {
        for (T item : args)
        {
            append(item);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new KelvinIterator<T>(this);
    }
}

class KelvinIterator<T> implements Iterator<T>
{
    Node currentObject;

    public KelvinIterator(KelvinList<T> list)
    {
        if (list.getSize() >0)
            currentObject = list.retrieve(0);
        else
            currentObject = null;
    }

    @Override
    public boolean hasNext() {
        return currentObject != null;
    }

    @Override
    public T next() {
        T data = (T) currentObject.data;
        currentObject = currentObject.nextNode;
        return data;
    }
}
