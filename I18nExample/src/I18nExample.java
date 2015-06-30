import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ƒемонстрирует работу с ResourceBundle.<br/>
 * ÷елесообразно поэкспериментировать, удал€€ (перемещ€€ в корень проекта)
 * различные пакеты ресурсов, а также убира€ (комментиру€) строку с ключем
 * hanger.<br/>
 * ƒл€ просмотра пакетов ресурсов можно использовать плагин <i>ResourceBundle
 * editor</i>.
 * 
 * @author Dmitry Kolesnikov
 */
public class I18nExample {
	public static void main(String[] args) throws Exception {
		Locale locale = new Locale("ru", "UA", "Kharkov");
		ResourceBundle rb = ResourceBundle.getBundle("resources", locale);
		System.out.println(rb.getString("hanger"));
	}
}
