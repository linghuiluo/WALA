public class B extends A {

  public static int y;

  public B() {}

  public void foo() {
    B.bar();
  }

  public static void bar() {
    y = 2;
  }
}
