package Tools;

//This is just a set of tools to make life easier.
public class PrintTools {
    public static void println(String text){System.out.println(text);}
    public static void errln(String text){System.err.println(text);}
    public static void print(String text){System.out.print(text);}
    public static void errprint(String text){System.err.print(text);}
    public static void  printStack(Exception e){
        StringBuilder stack=new StringBuilder();
        stack.append("<-----Begin Trace----->\n");
        for(int x=0;0<e.getStackTrace().length;x++){
            if(x==e.getStackTrace().length-1)
                break;
            stack.append("pos ->"+x +" "+ e.getStackTrace()[x]+" \n");
        }
        println(stack+"");
    }
    public static void print2DArrayInt(int[][] array) {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array.length; x++) {
                print(array[x][y] + " ");
            }
            println("");
        }
    }
    public static void print2DArrayString(String[][] array){
        for(int y=0;y<array.length;y++){
            for(int x=0;x<array.length;x++){
                print(array[x][y]+" ");
            }
            println("");
        }



    }


}