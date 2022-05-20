package com.example.fruitninja;

import android.graphics.Bitmap;

public class Node<T>
{
    private T value;
    private Node<T> next;

    public Node(T value)
    {
        this.value = value;
        this.next = null;
    }
    public Node(T value, Node<T> next)
    {
        this.value = value;
        this.next = next;
    }
    public T GetValue()
    {
        return this.value;
    }
    public Node<T> GetNext()
    {
        return this.next;
    }
    public boolean HasNext()
    {
        return (this.next != null);
    }
    public void SetValue(T value)
    {
        this.value = value;
    }
    public void SetNext(Node next)
    {
        this.next = next;
    }

    public int getLength(Node<T> node)
    {
        Node<T> p = node;
        int count = 0;

        while (p != null)
        {
            count++;
            p = p.GetNext();
        }
        return count;
    }

    public Node<T> getNode(int i, Node<T> node)
    {
        int count = 0;
        Node<T> p = node;

        if (i == 0)
            return node;
        else
        {
            while (count < i)
            {
                count++;
                p = p.GetNext();
            }
            return p;
        }
    }

    public Node<T> getLast(Node<T> node)
    {
        Node<T> p = node;

        while (p.GetNext() != null)
            p = p.GetNext();

        return p;
    }


    public int whatIsTheScore(Node<Fruit> fruitNode)
    {
        Node<Fruit> f = fruitNode;
        int count = 0;

        for (int i=0; i<fruitNode.getLength(fruitNode); i++)
        {
            if (f.GetValue().sliced == true)
                count++;
            f = f.GetNext();
        }
        return count;
    }

    public int howManyLosses(Node<Fruit> fruitNode, Node<String> fruitString)
    {
        Node<Fruit> f = fruitNode;
        Node<String> fS = fruitString;
        int count = 0;

        for (int i=0; i<fruitNode.getLength(fruitNode); i++)
        {
            if (fS.GetValue() != "bomb")
            {
                if (f.GetValue().getY() >= 950 && f.GetValue().sliced == false)
                    count++;
            }
            f = f.GetNext();
            fS = fS.GetNext();
        }
        return count;
    }

    public void insert(Node<T> node, Node<T> n)
    {
        Node<T> p = node;

        while (node.GetNext() != null)
            node = node.GetNext();

        node.SetNext(new Node(n));
        node = node.GetNext();
    }

    public void delete(Node<T> node, Node<T> n)
    {
        Node temp = node, prev = null;

        if (temp != null && temp.GetValue() == n.GetValue()) {
            node = temp.GetNext(); // Changed head
            return;
        }

        while (temp != null && temp.GetValue() != n.GetValue()) {
            prev = temp;
            temp = temp.GetNext();
        }

        if (temp == null)
            return;

        prev.SetNext(temp.GetNext());
    }

}
