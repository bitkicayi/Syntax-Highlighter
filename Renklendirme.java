import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
// Renklendirme sınıfı, Lexer’dan alınan List<Token>’ı kullanarak her token’ın tipine göre renk ve stil hazırlar.
public class Renklendirme {
    private final Map<Token.Tur, SimpleAttributeSet> turRenkMap; // Her tür için uygulanacak renk/stil buradan alınır.
    public Renklendirme() {
        this.turRenkMap = new HashMap<>();
        renkleriTanimla();
    }
    private void renkleriTanimla() { // Anahtar sözcük, sabit, yorum, operatör, ayırıcı vb için ayrı stiller oluştur
        // Anahtar sözcükler
        SimpleAttributeSet anahtarSet = new SimpleAttributeSet();
        StyleConstants.setForeground(anahtarSet, new Color(0x8B0000)); // Koyu Kırmızı
        StyleConstants.setBold(anahtarSet, true);
        Token.Tur[] anahtarSozcukler = {
                Token.Tur.ANAHTAR_SOZCUK_PACKAGE,
                Token.Tur.ANAHTAR_SOZCUK_IMPORT,
                Token.Tur.ANAHTAR_SOZCUK_CLASS,
                Token.Tur.ANAHTAR_SOZCUK_INTERFACE,
                Token.Tur.ANAHTAR_SOZCUK_EXTENDS,
                Token.Tur.ANAHTAR_SOZCUK_IMPLEMENTS,
                Token.Tur.ANAHTAR_SOZCUK_PUBLIC,
                Token.Tur.ANAHTAR_SOZCUK_PRIVATE,
                Token.Tur.ANAHTAR_SOZCUK_PROTECTED,
                Token.Tur.ANAHTAR_SOZCUK_STATIC,
                Token.Tur.ANAHTAR_SOZCUK_FINAL,
                Token.Tur.ANAHTAR_SOZCUK_VOID,
                Token.Tur.ANAHTAR_SOZCUK_INT,
                Token.Tur.ANAHTAR_SOZCUK_BOOLEAN,
                Token.Tur.ANAHTAR_SOZCUK_CHAR,
                Token.Tur.ANAHTAR_SOZCUK_DOUBLE,
                Token.Tur.ANAHTAR_SOZCUK_FLOAT,
                Token.Tur.ANAHTAR_SOZCUK_LONG,
                Token.Tur.ANAHTAR_SOZCUK_SHORT,
                Token.Tur.ANAHTAR_SOZCUK_BYTE,
                Token.Tur.ANAHTAR_SOZCUK_IF,
                Token.Tur.ANAHTAR_SOZCUK_ELSE,
                Token.Tur.ANAHTAR_SOZCUK_SWITCH,
                Token.Tur.ANAHTAR_SOZCUK_CASE,
                Token.Tur.ANAHTAR_SOZCUK_DEFAULT,
                Token.Tur.ANAHTAR_SOZCUK_FOR,
                Token.Tur.ANAHTAR_SOZCUK_WHILE,
                Token.Tur.ANAHTAR_SOZCUK_DO,
                Token.Tur.ANAHTAR_SOZCUK_BREAK,
                Token.Tur.ANAHTAR_SOZCUK_CONTINUE,
                Token.Tur.ANAHTAR_SOZCUK_RETURN,
                Token.Tur.ANAHTAR_SOZCUK_NEW,
                Token.Tur.ANAHTAR_SOZCUK_THIS,
                Token.Tur.ANAHTAR_SOZCUK_SUPER,
                Token.Tur.ANAHTAR_SOZCUK_THROW,
                Token.Tur.ANAHTAR_SOZCUK_THROWS,
                Token.Tur.ANAHTAR_SOZCUK_TRY,
                Token.Tur.ANAHTAR_SOZCUK_CATCH,
                Token.Tur.ANAHTAR_SOZCUK_FINALLY,
                Token.Tur.ANAHTAR_SOZCUK_SYNCHRONIZED,
                Token.Tur.ANAHTAR_SOZCUK_VOLATILE,
                Token.Tur.ANAHTAR_SOZCUK_TRANSIENT,
                Token.Tur.ANAHTAR_SOZCUK_ENUM,
                Token.Tur.ANAHTAR_SOZCUK_ASSERT,
                Token.Tur.ANAHTAR_SOZCUK_INSTANCEOF
        };
        for (Token.Tur t : anahtarSozcukler) {
            turRenkMap.put(t, anahtarSet);
        }
        // String Dizi Sabiti
        SimpleAttributeSet dizeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(dizeSet, new Color(0x008080)); // Turkuazımsı
        turRenkMap.put(Token.Tur.STRING_DIZI_SABIT, dizeSet);
        // Karakter sabiti
        SimpleAttributeSet karakterSet = new SimpleAttributeSet();
        StyleConstants.setForeground(karakterSet, new Color(0x006400)); // Koyu Yeşil
        turRenkMap.put(Token.Tur.KARAKTER_SABIT, karakterSet);
        // Sayı sabitleri ile Boolean/null
        SimpleAttributeSet sayiSet = new SimpleAttributeSet();
        StyleConstants.setForeground(sayiSet, new Color(0x800080)); // Mor
        turRenkMap.put(Token.Tur.TAM_SAYI_SABIT, sayiSet);
        turRenkMap.put(Token.Tur.ONDALIK_SAYI_SABIT, sayiSet);
        turRenkMap.put(Token.Tur.BOOLEAN_SABIT, sayiSet);
        turRenkMap.put(Token.Tur.NULL_SABIT, sayiSet);
        // Yorumlar
        SimpleAttributeSet yorumSet = new SimpleAttributeSet();
        StyleConstants.setForeground(yorumSet, new Color(0x000080)); // Koyu Mavi
        StyleConstants.setItalic(yorumSet, true);
        turRenkMap.put(Token.Tur.TEK_SATIR_YORUM, yorumSet);
        turRenkMap.put(Token.Tur.COK_SATIR_YORUM, yorumSet);
        // Operatörler
        SimpleAttributeSet operatorSet = new SimpleAttributeSet();
        StyleConstants.setForeground(operatorSet, new Color(0x8B4513)); // Turuncumsu
        Token.Tur[] operatorler = {
                Token.Tur.ARTIRMA_OPERATOR,
                Token.Tur.AZALTMA_OPERATOR,
                Token.Tur.ESITMI_OPERATOR,
                Token.Tur.NOT_ESITMI_OPERATOR,
                Token.Tur.NOT_OPERATOR,
                Token.Tur.KUCUK_ESITTIR,
                Token.Tur.BUYUK_ESITTIR,
                Token.Tur.KUCUKTUR_OP,
                Token.Tur.BUYUKTUR_OP,
                Token.Tur.VE_OPERATOR,
                Token.Tur.VEYA_OPERATOR,
                Token.Tur.SOL_KAYDIRMA,
                Token.Tur.SAG_KAYDIRMA,
                Token.Tur.UZUN_SAG_KAYDIRMA,
                Token.Tur.BITWISE_AND,
                Token.Tur.BITWISE_OR,
                Token.Tur.BITWISE_XOR,
                Token.Tur.ARTI_OP,
                Token.Tur.EKSI_OP,
                Token.Tur.CARPIM_OP,
                Token.Tur.BOLME_OP,
                Token.Tur.MOD_OP,
                Token.Tur.ATAMA_OPERATOR
        };
        for (Token.Tur t : operatorler) {
            turRenkMap.put(t, operatorSet);
        }
        // Ayırıcılar
        SimpleAttributeSet ayiriciSet = new SimpleAttributeSet();
        StyleConstants.setForeground(ayiriciSet, new Color(0x404040)); // Gri
        Token.Tur[] ayiricilar = {
                Token.Tur.NOKTALI_VIRGUL,
                Token.Tur.VIRGUL,
                Token.Tur.AC_PARANTEZ,
                Token.Tur.KAPA_PARANTEZ,
                Token.Tur.AC_SUSLU_PARANTEZ,
                Token.Tur.KAPA_SUSLU_PARANTEZ,
                Token.Tur.AC_KOSE_PARANTEZ,
                Token.Tur.KAPA_KOSE_PARANTEZ,
                Token.Tur.NOKTA,
                Token.Tur.UC_NOKTA
        };
        for (Token.Tur t : ayiricilar) {
            turRenkMap.put(t, ayiriciSet);
        }
        // Tanımlayıcı
        SimpleAttributeSet idSet = new SimpleAttributeSet();
        StyleConstants.setForeground(idSet, new Color(0x556B2F)); // Zeytin Yeşili
        turRenkMap.put(Token.Tur.TANIMLAYICI, idSet);
        // Diğer ve Boşluk/YeniSatır/Sekme
        SimpleAttributeSet digerSet = new SimpleAttributeSet();
        StyleConstants.setForeground(digerSet, new Color(0xD3D3D3)); // Beyaz
        Token.Tur[] digerTurler = {
                Token.Tur.DIGER,
                Token.Tur.BOSLUK,
                Token.Tur.YENI_SATIR,
                Token.Tur.SEKME
        };
        for (Token.Tur t : digerTurler) {
            turRenkMap.put(t, digerSet);
        }
    }
    // Lexer’dan alınan token listesine göre her bir token’ın başlangıç indeksi, uzunluk ve stil bilgilerini içeren ColorSpan listesine dön
    public List<ColorSpan> renklendir(List<Token> tokenlar) {
        java.util.ArrayList<ColorSpan> spans = new java.util.ArrayList<>();
        for (Token token : tokenlar) {
            Token.Tur tur = token.tokenTurAl();
            SimpleAttributeSet stil = turRenkMap.getOrDefault(tur, digerStil());
            int baslangic = token.baslangicIndexAl();
            int uzunluk = token.bitisIndexAl() - baslangic;
            spans.add(new ColorSpan(baslangic, uzunluk, stil));
        }
        return spans;
    }
    // Eğer bir Token.Tur eşleşmesi bulunamazsa kullanılacak varsayılan stil
    private SimpleAttributeSet digerStil() {
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, Color.LIGHT_GRAY); // Açık Gri
        return s;
    }
    public static class ColorSpan { // Bir token’ın başlangıç indeksi, uzunluğu ve stilini taşır.
        private final int baslangic;
        private final int uzunluk;
        private final SimpleAttributeSet stil;
        public ColorSpan(int baslangic, int uzunluk, SimpleAttributeSet stil) {
            this.baslangic = baslangic;
            this.uzunluk = uzunluk;
            this.stil = stil;
        }
        public int getBaslangic() {
            return baslangic;
        }
        public int getUzunluk() {
            return uzunluk;
        }
        public SimpleAttributeSet getStil() {
            return stil;
        }}}