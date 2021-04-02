package src.position;

import java.util.LinkedList;

public class PositionMap<E>
{
    private LinkedList<E>[] mapE;
    private LinkedList<Position>[] mapP;
    private int size;

    public PositionMap(int size) {
        if(size == 0)
            size++;
        this.size = size;
        mapE = new LinkedList[size];
        for (int i = 0; i < mapE.length; i++)
            mapE[i] = new LinkedList<>();

        mapP = new LinkedList[size];
        for (int i = 0; i < mapP.length; i++)
            mapP[i] = new LinkedList<>();
    }

    public void add(Position position, E e) {

        int pos = calculate(position) % size;
        mapE[pos].add(e);
        mapP[pos].add(position);
    }

    public E get(Position position) {
        int pos = calculate(position) % size;
        int index = mapP[pos].indexOf(position);
        if (index != -1)
        {
            System.out.printf("BOARD MATCH @ %s%n", position);
            return mapE[pos].get(index);
        }
        return null;
    }

    public E remove(Position position) {
        int pos = calculate(position) % size;
        int index = mapP[pos].indexOf(position);
        if (index != -1)
        {
            System.out.printf("BOARD MATCH @ %s%n", position);
            mapP[pos].remove(index);
            return mapE[pos].remove(index);
        }
        return null;
    }

    private int calculate(Position position) {
        int k1 = position.getRow();
        int k2 = position.getCol();
        return (k1 + k2) * (k1 + k2 + 1) / 2 + k2;
    }
}
