import java.util.ArrayList;
import java.util.Iterator;

public class ListDemo {

    public static void main(String[] args) {
        //array(disadvantage->fixed in length)
        float n = 0.734123421341234f;
        Object[] list = new Object[10];
        list[1]=1;
        list[6]="sai";
        list[7]=n;
        System.out.println("array : ");
        for(Object intlist : list) {
            System.out.print(intlist+" ");
        }
        System.out.println();
        //ArrayList
        ArrayList<Object> ls =new ArrayList<>();
        ls.add(1);
        ls.add("sai");
        ls.add(1.7);
        ls.add(0.32213234234f);
        //iterator used
        Iterator<Object> iterator = ls.iterator();
        System.out.println("arrayList : ");
        while(iterator.hasNext()) {
            System.out.print(iterator.next()+" ");
        }

    }

}
