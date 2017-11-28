package number;

import dictionary.Dictionary;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Создано Koldushko 27.11.2017
 * @autor Sergey Koldushko
 */
public class Converter {
    /*Const*/
    private final static char ZERO = '0';
    private final static char MINUS = '-';
    private final static String PATH_DICTIONARY = "src/main/resources/dictionary.txt";
    private final static String DEGREE_OF_TEN = "10^";

    private String numberStringFormat;
    private Logger log = Logger.getLogger(Converter.class.getName());
    private Dictionary dictionary = new Dictionary();
    public String toWords(int number) {
        return toWords(BigInteger.valueOf(number));
    }

    public String toWords(long number) {
        return toWords(BigInteger.valueOf(number));
    }

    public String toWords(String number) {
        try {
            return toWords(new BigInteger(number));
        } catch (NumberFormatException ex) {
            log.error("Invalid input format " + number);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }


    public String toWords(BigInteger number) {
        try {
            StringBuilder result = new StringBuilder();
            numberStringFormat = String.valueOf(number);
            //загрузка файла со словами эквивалентные цифрам
            dictionary.load(PATH_DICTIONARY);
            //проверка на отрицательность и нулевое значение
            result.append(minusAndZero());
            //синтаксический разбор строки
            result = parser(result);
            log.info("Converted number " + number + " in words " + result.toString());
            return result.toString();
        } catch (IOException ex){
            log.error("A dictionary with words equivalent to numbers was not loaded.");
            throw new RuntimeException("A dictionary with words equivalent to numbers was not loaded.");
        }
    }

    /*
    * Метод рекурсивно выполняет синтаксический разбор строки
    * от первого до последнего символа в числе.
     */
    private StringBuilder parser(StringBuilder result){
        if(numberStringFormat.length() == 0) return result;
        switch (numberStringFormat.length()) {
            //единицы
            case 1:
                result.append(units());
                correctString(result);
                return result;
            //десятки
            case 2:
                result.append(ten());
                break;
            //сотни
            case 3:
                result.append(hundreds());
                break;
            //тысячные
            case 4:
                result.append(thousands());
                break;
            default:
                //числа больше тысячнах имеют закономерность
                switch ((numberStringFormat.length() - 1) % 3) {
                    //милионы, милиарды, трилиарды и т.д.
                    //данные числа имеют закономерность в склонениях
                    case 0:
                        result.append(other());
                        break;
                    //десяти-
                    case 1:
                        result.append(ten());
                        break;
                    //сто-
                    case 2:
                        //если сто-, десяти-, -ионны равны 0, то данные разряды пропускаются
                        if (numberStringFormat.substring(0, 3).equals("000"))
                            numberStringFormat = numberStringFormat.substring(3);
                        else
                            result.append(hundreds());
                        break;
                }
        }
        result = parser(result);
        return result;
    }

    public static void main(String[] args) {
        Converter converter = new Converter();
        converter.toWords(121241241);
    }


    private String minusAndZero() {
        String result = "";
        if (numberStringFormat.charAt(0) == MINUS) {
            result = dictionary.get(String.valueOf(MINUS)) + " ";
            numberStringFormat = deleteFirstChartInString(numberStringFormat);
        } else if (numberStringFormat.charAt(0) == ZERO && numberStringFormat.length()==1) {
            result = dictionary.get(String.valueOf(ZERO));
            numberStringFormat = deleteFirstChartInString(numberStringFormat);
        }
        return result;
    }


    private String units() {
        if (numberStringFormat.charAt(0) != ZERO) {
            return dictionary.get(numberStringFormat.substring(0, 1));
        }
        return "";
    }


    private String ten() {
        String result;
        switch (numberStringFormat.charAt(0)) {
            case ZERO:
                result = "";
                break;
            case '1':
                result = dictionary.get(numberStringFormat.substring(0, 2)) + " ";
                numberStringFormat = replaceCharAt(numberStringFormat, 1, ZERO);
                break;
            default:
                result = dictionary.get(ceilNumberInStringFormat(numberStringFormat.substring(0, 2), 1)) + " ";
                break;
        }
        numberStringFormat = deleteFirstChartInString(numberStringFormat);
        return result;
    }


    private String hundreds() {
        String result;
        if (numberStringFormat.charAt(0) != ZERO)
            result = dictionary.get(
                    ceilNumberInStringFormat(numberStringFormat.substring(0, 3), 2)) + " ";
        else result = "";
        numberStringFormat = deleteFirstChartInString(numberStringFormat);
        return result;
    }


    private String thousands() {
        String result;
        String degreeOfTen = DEGREE_OF_TEN + (numberStringFormat.length() - 1);
        if (numberStringFormat.charAt(0) == ZERO) result = dictionary.get(degreeOfTen) + " ";
        else if (numberStringFormat.charAt(0) >= '1' && numberStringFormat.charAt(0) <= '4')
            result = dictionary.get(ceilNumberInStringFormat(numberStringFormat, numberStringFormat.length() - 1)) + " ";
        else
            result = units() + " " + dictionary.get(degreeOfTen) + " ";

        numberStringFormat = deleteFirstChartInString(numberStringFormat);
        return result;
    }


    private String other() {
        String result;
        String degreeOfTen = DEGREE_OF_TEN + (numberStringFormat.length() - 1);
        if (numberStringFormat.charAt(0) == ZERO)
            result = dictionary.get(degreeOfTen) + "ов" + " ";
        else if (numberStringFormat.charAt(0) == '1')
            result = units() + " " + dictionary.get(degreeOfTen) + " ";
        else if (numberStringFormat.charAt(0) >= '2' && numberStringFormat.charAt(0) <= '4')
            result = units() + " " + dictionary.get(degreeOfTen) + "а" + " ";
        else
            result = units() + " " + dictionary.get(degreeOfTen) + "ов" + " ";

        numberStringFormat = deleteFirstChartInString(numberStringFormat);
        if (result.contains("null")) throw new NullPointerException("The number of a given degree is not in the dictionary " + degreeOfTen);
        return result;
    }

    /*
    * Метод заменяет нулями n знаков до запятой без округления.
    * Возращает округленное число в строковом формате.
     */
    private String ceilNumberInStringFormat(String numberStringFormat, int n) {
        long number = Long.valueOf(numberStringFormat);
        //округленное число
        long ceilNumber = (long) (((long) (number / Math.pow(10, n))) * Math.pow(10, n));
        return String.valueOf(ceilNumber);
    }

    /*
    * Данный метод удаляет лишние пробелы
     */
    private void correctString(StringBuilder result) { //TODO исправить добавление пробелов
        if (result.charAt(result.length() - 1) == ' ')
            result.deleteCharAt(result.length() - 1);
    }

    /*
    * Удаляет первый символ из строки.
     */
    private String deleteFirstChartInString(final String str) {
        return str.substring(1);
    }

    /*
    * Заменяет n-ый символ в строк на указанный.
    */
    private String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }
}
