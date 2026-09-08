void main() {
Scanner scan = new Scanner(System.in);
System.out.println("Застрял и не можешь придумать следующий ход? Не беда! Я помогу тебе;)");
System.out.println("Для начала скажи, ты хочешь загрузить шахматную ситуацию или расставить фигуры самостоятельно?");
System.out.println("1 - загрузить");
System.out.println("2 - расставить самостоятельно");
int desk = scan.nextInt();
CreateDesk.createDesk(desk);

System.out.println("Хорошо, перейдем к игре. Выбери, фигура в какой клетке тебя интересует");
}
