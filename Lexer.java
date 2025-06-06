import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* Lexer sınıfı, verilen kaynak kod metnini baştan sona tarayıp Token sınıfında tanımlı enum Tur değerlerine göre parçalara (tokenlara) ayırır.
   Her bir eşleşen alt dize için bir Token nesnesi oluşturur ve bunları  bir List<Token> olarak döner. */
public class Lexer {
    //  ----  Alanlar  ----
    /* regexKurallari haritası, sırayla kontrol edilecek Pattern -> Token.Tur eşlemesini barındırır.
       LinkedHashMap kullanmamızın nedeni, ekleme sırasının korunmasıdır. Böylece özel (anahtar sözcük, yorum vb.) kalıplar
       daha genel kalıplardan (identifier, sayı vb.) önce gelir. */
    private final Map<Pattern, Token.Tur> regexKurallari;
    /* BOSLUK_PATTERN, satır başı, boşluk ve sekme gibi "yoksayılacak" karakterler için kullanılacak desendir.
       Kalan metnin başında eğer boşluk, tab veya yeni satır varsa, bunları atlayıp gerçek tokenları aramaya devam eder. */
    private static final Pattern BOSLUK_PATTERN = Pattern.compile("^\\s+");
    public Lexer() {
        regexKurallari = new LinkedHashMap<>();
        //  ----Anahtar Sözcükler  ----
        regexKurallari.put(Pattern.compile("^\\bpackage\\b"), Token.Tur.ANAHTAR_SOZCUK_PACKAGE);
        regexKurallari.put(Pattern.compile("^\\bimport\\b"), Token.Tur.ANAHTAR_SOZCUK_IMPORT);
        regexKurallari.put(Pattern.compile("^\\bclass\\b"), Token.Tur.ANAHTAR_SOZCUK_CLASS);
        regexKurallari.put(Pattern.compile("^\\binterface\\b"), Token.Tur.ANAHTAR_SOZCUK_INTERFACE);
        regexKurallari.put(Pattern.compile("^\\bextends\\b"), Token.Tur.ANAHTAR_SOZCUK_EXTENDS);
        regexKurallari.put(Pattern.compile("^\\bimplements\\b"), Token.Tur.ANAHTAR_SOZCUK_IMPLEMENTS);
        regexKurallari.put(Pattern.compile("^\\bpublic\\b"), Token.Tur.ANAHTAR_SOZCUK_PUBLIC);
        regexKurallari.put(Pattern.compile("^\\bprivate\\b"), Token.Tur.ANAHTAR_SOZCUK_PRIVATE);
        regexKurallari.put(Pattern.compile("^\\bprotected\\b"), Token.Tur.ANAHTAR_SOZCUK_PROTECTED);
        regexKurallari.put(Pattern.compile("^\\bstatic\\b"), Token.Tur.ANAHTAR_SOZCUK_STATIC);
        regexKurallari.put(Pattern.compile("^\\bfinal\\b"), Token.Tur.ANAHTAR_SOZCUK_FINAL);
        regexKurallari.put(Pattern.compile("^\\bvoid\\b"), Token.Tur.ANAHTAR_SOZCUK_VOID);
        regexKurallari.put(Pattern.compile("^\\bint\\b"), Token.Tur.ANAHTAR_SOZCUK_INT);
        regexKurallari.put(Pattern.compile("^\\bboolean\\b"), Token.Tur.ANAHTAR_SOZCUK_BOOLEAN);
        regexKurallari.put(Pattern.compile("^\\bchar\\b"), Token.Tur.ANAHTAR_SOZCUK_CHAR);
        regexKurallari.put(Pattern.compile("^\\bdouble\\b"), Token.Tur.ANAHTAR_SOZCUK_DOUBLE);
        regexKurallari.put(Pattern.compile("^\\bfloat\\b"), Token.Tur.ANAHTAR_SOZCUK_FLOAT);
        regexKurallari.put(Pattern.compile("^\\blong\\b"), Token.Tur.ANAHTAR_SOZCUK_LONG);
        regexKurallari.put(Pattern.compile("^\\bshort\\b"), Token.Tur.ANAHTAR_SOZCUK_SHORT);
        regexKurallari.put(Pattern.compile("^\\bbyte\\b"), Token.Tur.ANAHTAR_SOZCUK_BYTE);
        regexKurallari.put(Pattern.compile("^\\bif\\b"), Token.Tur.ANAHTAR_SOZCUK_IF);
        regexKurallari.put(Pattern.compile("^\\belse\\b"), Token.Tur.ANAHTAR_SOZCUK_ELSE);
        regexKurallari.put(Pattern.compile("^\\bswitch\\b"), Token.Tur.ANAHTAR_SOZCUK_SWITCH);
        regexKurallari.put(Pattern.compile("^\\bcase\\b"), Token.Tur.ANAHTAR_SOZCUK_CASE);
        regexKurallari.put(Pattern.compile("^\\bdefault\\b"), Token.Tur.ANAHTAR_SOZCUK_DEFAULT);
        regexKurallari.put(Pattern.compile("^\\bfor\\b"), Token.Tur.ANAHTAR_SOZCUK_FOR);
        regexKurallari.put(Pattern.compile("^\\bwhile\\b"), Token.Tur.ANAHTAR_SOZCUK_WHILE);
        regexKurallari.put(Pattern.compile("^\\bdo\\b"), Token.Tur.ANAHTAR_SOZCUK_DO);
        regexKurallari.put(Pattern.compile("^\\bbreak\\b"), Token.Tur.ANAHTAR_SOZCUK_BREAK);
        regexKurallari.put(Pattern.compile("^\\bcontinue\\b"), Token.Tur.ANAHTAR_SOZCUK_CONTINUE);
        regexKurallari.put(Pattern.compile("^\\breturn\\b"), Token.Tur.ANAHTAR_SOZCUK_RETURN);
        regexKurallari.put(Pattern.compile("^\\bnew\\b"), Token.Tur.ANAHTAR_SOZCUK_NEW);
        regexKurallari.put(Pattern.compile("^\\bthis\\b"), Token.Tur.ANAHTAR_SOZCUK_THIS);
        regexKurallari.put(Pattern.compile("^\\bsuper\\b"), Token.Tur.ANAHTAR_SOZCUK_SUPER);
        regexKurallari.put(Pattern.compile("^\\bthrow\\b"), Token.Tur.ANAHTAR_SOZCUK_THROW);
        regexKurallari.put(Pattern.compile("^\\bthrows\\b"), Token.Tur.ANAHTAR_SOZCUK_THROWS);
        regexKurallari.put(Pattern.compile("^\\btry\\b"), Token.Tur.ANAHTAR_SOZCUK_TRY);
        regexKurallari.put(Pattern.compile("^\\bcatch\\b"), Token.Tur.ANAHTAR_SOZCUK_CATCH);
        regexKurallari.put(Pattern.compile("^\\bfinally\\b"), Token.Tur.ANAHTAR_SOZCUK_FINALLY);
        regexKurallari.put(Pattern.compile("^\\bsynchronized\\b"), Token.Tur.ANAHTAR_SOZCUK_SYNCHRONIZED);
        regexKurallari.put(Pattern.compile("^\\bvolatile\\b"), Token.Tur.ANAHTAR_SOZCUK_VOLATILE);
        regexKurallari.put(Pattern.compile("^\\btransient\\b"), Token.Tur.ANAHTAR_SOZCUK_TRANSIENT);
        regexKurallari.put(Pattern.compile("^\\benum\\b"), Token.Tur.ANAHTAR_SOZCUK_ENUM);
        regexKurallari.put(Pattern.compile("^\\bassert\\b"), Token.Tur.ANAHTAR_SOZCUK_ASSERT);
        regexKurallari.put(Pattern.compile("^\\binstanceof\\b"), Token.Tur.ANAHTAR_SOZCUK_INSTANCEOF);
        // ----  Yorum Türleri  ----
        regexKurallari.put(Pattern.compile("^/\\*[\\s\\S]*?\\*/"), Token.Tur.COK_SATIR_YORUM); // Çok satırlı yorum /* ... */
        regexKurallari.put(Pattern.compile("^//.*"), Token.Tur.TEK_SATIR_YORUM); // Tek satırlı yorum: // ...
        regexKurallari.put(Pattern.compile("^\"(\\\\.|[^\"\\\\])*\""), Token.Tur.STRING_DIZI_SABIT);  // String Dizi sabiti
        regexKurallari.put(Pattern.compile("^'(\\\\.|[^'\\\\])'"), Token.Tur.KARAKTER_SABIT); // Karakter sabiti: char
        // ----  Sabit Türleri  ----
        regexKurallari.put(Pattern.compile("^\\b\\d+\\.\\d+\\b"), Token.Tur.ONDALIK_SAYI_SABIT); // Ondalıklı sayı sabiti
        regexKurallari.put(Pattern.compile("^\\b\\d+\\b"), Token.Tur.TAM_SAYI_SABIT); // Tam sayı sabiti
        regexKurallari.put(Pattern.compile("^\\b(true|false)\\b"), Token.Tur.BOOLEAN_SABIT); // Boolean sabiti
        regexKurallari.put(Pattern.compile("^\\bnull\\b"), Token.Tur.NULL_SABIT); // Null sabiti
        // ----  Operatörler ----
        // not: Sıranın önemli olduğunu deneyimledim.
        regexKurallari.put(Pattern.compile("^>>>"), Token.Tur.UZUN_SAG_KAYDIRMA); // >>>
        regexKurallari.put(Pattern.compile("^>>"), Token.Tur.SAG_KAYDIRMA); // >>
        regexKurallari.put(Pattern.compile("^<<"), Token.Tur.SOL_KAYDIRMA); // <<
        regexKurallari.put(Pattern.compile("^\\+\\+"), Token.Tur.ARTIRMA_OPERATOR); // ++
        regexKurallari.put(Pattern.compile("^--"), Token.Tur.AZALTMA_OPERATOR); // --
        regexKurallari.put(Pattern.compile("^=="), Token.Tur.ESITMI_OPERATOR); // ==
        regexKurallari.put(Pattern.compile("^!="), Token.Tur.NOT_ESITMI_OPERATOR); // !=
        regexKurallari.put(Pattern.compile("^!"), Token.Tur.NOT_OPERATOR); // !
        regexKurallari.put(Pattern.compile("^<="), Token.Tur.KUCUK_ESITTIR); // <=
        regexKurallari.put(Pattern.compile("^>="), Token.Tur.BUYUK_ESITTIR); // >=
        regexKurallari.put(Pattern.compile("^<"), Token.Tur.KUCUKTUR_OP); // <
        regexKurallari.put(Pattern.compile("^>"), Token.Tur.BUYUKTUR_OP); // >
        regexKurallari.put(Pattern.compile("^&&"), Token.Tur.VE_OPERATOR); // &&
        regexKurallari.put(Pattern.compile("^\\|\\|"), Token.Tur.VEYA_OPERATOR); // ||
        regexKurallari.put(Pattern.compile("^&"), Token.Tur.BITWISE_AND); // &
        regexKurallari.put(Pattern.compile("^\\|"), Token.Tur.BITWISE_OR); // |
        regexKurallari.put(Pattern.compile("^\\^"), Token.Tur.BITWISE_XOR); // ^
        regexKurallari.put(Pattern.compile("^\\+"), Token.Tur.ARTI_OP); // +
        regexKurallari.put(Pattern.compile("^-"), Token.Tur.EKSI_OP); // -
        regexKurallari.put(Pattern.compile("^\\*"), Token.Tur.CARPIM_OP); // *
        regexKurallari.put(Pattern.compile("^/"), Token.Tur.BOLME_OP); // /
        regexKurallari.put(Pattern.compile("^%"), Token.Tur.MOD_OP); // %
        regexKurallari.put(Pattern.compile("^="), Token.Tur.ATAMA_OPERATOR); // =
        // ----  Ayırıcılar  ----
        regexKurallari.put(Pattern.compile("^;"), Token.Tur.NOKTALI_VIRGUL); // ;
        regexKurallari.put(Pattern.compile("^,"), Token.Tur.VIRGUL); // ,
        regexKurallari.put(Pattern.compile("^\\("), Token.Tur.AC_PARANTEZ); // (
        regexKurallari.put(Pattern.compile("^\\)"), Token.Tur.KAPA_PARANTEZ); // )
        regexKurallari.put(Pattern.compile("^\\{"), Token.Tur.AC_SUSLU_PARANTEZ); // {
        regexKurallari.put(Pattern.compile("^\\}"), Token.Tur.KAPA_SUSLU_PARANTEZ); // }
        regexKurallari.put(Pattern.compile("^\\["), Token.Tur.AC_KOSE_PARANTEZ); // [
        regexKurallari.put(Pattern.compile("^\\]"), Token.Tur.KAPA_KOSE_PARANTEZ); // ]
        regexKurallari.put(Pattern.compile("^\\.\\.\\."), Token.Tur.UC_NOKTA); // ...
        regexKurallari.put(Pattern.compile("^\\."), Token.Tur.NOKTA); // .
        // ----  Tanımlayıcı ----
        /* Harf veya alt çizgi ile başlar, ardından harf, rakam veya alt çizgi olabilir.
        Anahtar sözcükler yukarıda tanımlı olduğu için, kalan uygun yapılar tanımlayıcı olarak kabul edilir. */
        regexKurallari.put(Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*"), Token.Tur.TANIMLAYICI);
    }
    // ----  Metodlar  ----
    /* parcalama metodu, parametre olarak aldığı kaynak metni baştan sona tarar. Her adımda önce baştaki boşluk/sekme/yeni satır varsa atlar,
       sonra regexKurallari içindeki kalıplarla sırayla en uzun eşleşmeyi bulur. Eşleşen metni, bir Token nesnesine dönüştürür ve listeye ekler.
       Eğer hiçbir kalıp eşleşmezse, tek bir karakter DIGER türünde token olarak alınır. */
    public List<Token> parcalama(String kaynakMetin) {
        List<Token> tokenListesi = new ArrayList<>();
        int index = 0;
        while (index < kaynakMetin.length()) {
            String kalanMetin = kaynakMetin.substring(index); // Kalan metni, şu anki index’ten itibaren al
            Matcher boslukMatcher = BOSLUK_PATTERN.matcher(kalanMetin); // Başta boşluk, sekme veya yeni satır varsa, bunları atla
            if (boslukMatcher.find()) {
                int boslukUzunluk = boslukMatcher.end(); // Eğer ^\\s+ ile eşleşme varsa, atlamamız gereken uzunluk
                index += boslukUzunluk;
                continue;  // Döngünün başına döner, yeni index ile devam et
            }
            // Hiç boşluk yoksa, regexKurallari içindeki her bir Desen’i sırayla dene
            boolean eslesti = false;  // Eşleşme durumu için ilk değer
            for (Map.Entry<Pattern, Token.Tur> giris : regexKurallari.entrySet()) {
                Pattern desen = giris.getKey();
                Token.Tur tur = giris.getValue();
                Matcher matcher = desen.matcher(kalanMetin);
                if (matcher.find()) {
                    String eslesenMetin = matcher.group(); // Eşleşme yalnızca kalanMetin’in başında ^ ile kontrol et
                    int eslesenUzunluk = eslesenMetin.length();
                    Token token = new Token(); // Yeni bir Token nesnesi oluşturup tüm alanları ata
                    token.tokenTurAta(tur); // Türü ata
                    token.icerikAta(eslesenMetin); // İçeriği ata
                    token.baslangicIndexAta(index); // Başlangıç index’ini ata
                    token.bitisIndexAta(index + eslesenUzunluk); // Bitiş index’ini ata
                    tokenListesi.add(token); // Listeye ekle
                    index += eslesenUzunluk; // İmleci, eşleşen uzunluk kadar ilerlet
                    eslesti = true;
                    break;
                }
            }
            // Eğer hiçbir kalıp eşleşmediyse, tek bir karakteri DIGER olarak işaretle
            if (!eslesti) {
                char tekKarakter = kalanMetin.charAt(0);
                Token token = new Token();
                token.tokenTurAta(Token.Tur.DIGER);
                token.icerikAta(String.valueOf(tekKarakter));
                token.baslangicIndexAta(index);
                token.bitisIndexAta(index + 1);
                tokenListesi.add(token);
                index += 1;  // İmleci bir karakter ilerlet
            }
        }
        // Tüm metin taranıp token listesi oluşturulduktan sonra listeyi döndür
        return tokenListesi;
    }}