import java.util.ArrayList;
import java.util.List;


public class TestGenerics6
{
    private void why()
    {
        List list = new ArrayList();
        list.add("123");
        Object object = list.get(0);
        String string = (String) list.get(0);
        Integer integer = (Integer) list.get(0);
        
        List<String> listOfString = new ArrayList();
        listOfString.add("123");
        Object anotherObject = listOfString.get(0);
        String anotherString = listOfString.get(0);
        Integer anotherInteger = (Integer) listOfString.get(0);
        

    }
    
    public static void main(String[] args)
    {
        List<String> list = new ArrayList();
        list(list);
        listOfUnknown(list);
        listOfObject(list);
        listOfString(list);
        listOfInteger(list);
        listOfCharSeq(list);
        listOfExtendsCharSeq(list);
        listOfSuperCharSeq(list);
    }

    private static void list(List list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        
        Object object = list.get(1);
    }
    private static void listOfObject(List<Object> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        Object object = list.get(1);
    }
    private static void listOfUnknown(List<?> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        Object x = list.get(1);
    }
    
    private static void listOfInteger()
    {
        List<Integer> list = new ArrayList();
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        Integer integer = list.get(1);
    }
    
    private static void listOfString(List<String> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        String string = list.get(1);
    }
    
    private static void listOfCharSeq(List<CharSequence> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        CharSequence charSequence = list.get(1);
    }
    
    private static void listOfExtendsCharSeq(List<? extends CharSequence> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        CharSequence charSequence = list.get(1);
    }
    
    private static void listOfSuperCharSeq(List<? super CharSequence> list)
    {
        list.add("123");
        list.add("123".subSequence(1, 3));
        list.add(new StringBuilder());
        list.add(Integer.valueOf(1));
        list.add(new Object());
        
        Object charSequence = list.get(1);
    }
    
    private static void listOfStringIsNotAListOfObject()
    {
        List<String> listOfString = new ArrayList();
        List<Object> listOfObject = listOfString;

        listOfObject.add(new Object());
        String s = listOfString.get(0);
    }
}
