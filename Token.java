import java.util.Objects;
/* Token sınıfı, kaynak kodu parçalama işlemi sonucunda elde edilen en küçük anlamsal birimi (kelime, sabit, operatör, yorum vb.) temsil eder.
   Bu sınıf, her bir token’ın türünü, ham metin içeriğini ve metin içindeki konumunu saklar. */
public class Token {
   // Tur enum’u, her bir Token’ın hangi kategoride olduğunu belirtir. Java diline özgü yapısal öğeler ve sabit türleri de dahil edilmiştir.
    public enum Tur {
        //Anahtar Sözcükler
        ANAHTAR_SOZCUK_PACKAGE, // "package"
        ANAHTAR_SOZCUK_IMPORT, // "import"
        ANAHTAR_SOZCUK_CLASS, // "class"
        ANAHTAR_SOZCUK_INTERFACE, // "interface"
        ANAHTAR_SOZCUK_EXTENDS, // "extends"
        ANAHTAR_SOZCUK_IMPLEMENTS, // "implements"
        ANAHTAR_SOZCUK_PUBLIC, // "public"
        ANAHTAR_SOZCUK_PRIVATE, // "private"
        ANAHTAR_SOZCUK_PROTECTED, // "protected"
        ANAHTAR_SOZCUK_STATIC, // "static"
        ANAHTAR_SOZCUK_FINAL, // "final"
        ANAHTAR_SOZCUK_VOID, // "void"
        ANAHTAR_SOZCUK_INT, // "int"
        ANAHTAR_SOZCUK_BOOLEAN, // "boolean"
        ANAHTAR_SOZCUK_CHAR, // "char"
        ANAHTAR_SOZCUK_DOUBLE, // "double"
        ANAHTAR_SOZCUK_FLOAT, // "float"
        ANAHTAR_SOZCUK_LONG, // "long"
        ANAHTAR_SOZCUK_SHORT, // "short"
        ANAHTAR_SOZCUK_BYTE, // "byte"
        ANAHTAR_SOZCUK_IF, // "if"
        ANAHTAR_SOZCUK_ELSE, // "else"
        ANAHTAR_SOZCUK_SWITCH, // "switch"
        ANAHTAR_SOZCUK_CASE, // "case"
        ANAHTAR_SOZCUK_DEFAULT, // "default"
        ANAHTAR_SOZCUK_FOR, // "for"
        ANAHTAR_SOZCUK_WHILE, // "while"
        ANAHTAR_SOZCUK_DO, // "do"
        ANAHTAR_SOZCUK_BREAK, // "break"
        ANAHTAR_SOZCUK_CONTINUE, // "continue"
        ANAHTAR_SOZCUK_RETURN, // "return"
        ANAHTAR_SOZCUK_NEW, // "new"
        ANAHTAR_SOZCUK_THIS, // "this"
        ANAHTAR_SOZCUK_SUPER, // "super"
        ANAHTAR_SOZCUK_THROW, // "throw"
        ANAHTAR_SOZCUK_THROWS, // "throws"
        ANAHTAR_SOZCUK_TRY, // "try"
        ANAHTAR_SOZCUK_CATCH, // "catch"
        ANAHTAR_SOZCUK_FINALLY, // "finally"
        ANAHTAR_SOZCUK_SYNCHRONIZED, // "synchronized"
        ANAHTAR_SOZCUK_VOLATILE, // "volatile"
        ANAHTAR_SOZCUK_TRANSIENT, // "transient"
        ANAHTAR_SOZCUK_ENUM, // "enum"
        ANAHTAR_SOZCUK_ASSERT, // "assert"
        ANAHTAR_SOZCUK_INSTANCEOF, // "instanceof"
        //Sabit Türleri
        TAM_SAYI_SABIT, // Tam sayı sabiti
        ONDALIK_SAYI_SABIT, // Ondalıklı sayı sabiti
        KARAKTER_SABIT, // Tek tırnak içinde karakter
        STRING_DIZI_SABIT, // Çift tırnak içinde metin
        BOOLEAN_SABIT, // true veya false
        NULL_SABIT, // null
        TANIMLAYICI, // Değişken, metot, sınıf adları
        //Operatörler
        ARTIRMA_OPERATOR, // ++
        AZALTMA_OPERATOR, // --
        ESITMI_OPERATOR, // ==
        NOT_ESITMI_OPERATOR, // !=
        NOT_OPERATOR, // !
        KUCUKTUR_OP, // <
        BUYUKTUR_OP, // >
        KUCUK_ESITTIR, // <=
        BUYUK_ESITTIR, // >=
        VE_OPERATOR, // &&
        VEYA_OPERATOR, // ||
        ATAMA_OPERATOR, // =
        ARTI_OP, // +
        EKSI_OP, // -
        CARPIM_OP, // *
        BOLME_OP, // /
        MOD_OP, // %
        BITWISE_AND, // &
        BITWISE_OR, // |
        BITWISE_XOR, // ^
        SOL_KAYDIRMA, // <<
        SAG_KAYDIRMA, // >>
        UZUN_SAG_KAYDIRMA, // >>>
        //Ayırıcılar
        NOKTALI_VIRGUL, // ;
        VIRGUL, // ,
        AC_PARANTEZ, // (
        KAPA_PARANTEZ, // )
        AC_SUSLU_PARANTEZ, // {
        KAPA_SUSLU_PARANTEZ, // }
        AC_KOSE_PARANTEZ, // [
        KAPA_KOSE_PARANTEZ, // ]
        NOKTA, // .
        UC_NOKTA, // ...
        //Yorum Türleri (Comment)
        TEK_SATIR_YORUM, // // ...
        COK_SATIR_YORUM, // /* ... */
        //Boşluk / Yeni Satır / Sekme
        BOSLUK, // ’ ’, ’\t’
        YENI_SATIR, // ’\n’
        SEKME,  // ’\r’, ’\r\n’ veya ’\t’
        //Diğer
        DIGER // Tanımlanmayan veya işlenemeyen karakterler
    }
    //Alanlar
    private Tur tokenTur; // Bu token’ın enum Tur içerisindeki türünü saklar.
    private String icerik; // Token’ın kaynak kod içindeki ham metin içeriği. Örneğin "public", "123", "\"Merhaba\"" vb.
    private int baslangicIndex; // Kaynak metin içindeki token’ın başladığı karakterin indeksini saklar.
    private int bitisIndex; // Kaynak metin içindeki token’ın bittiği karakterin bir sonraki indeksi saklar.
    //Kurucu Metotlar
    public Token() {} // Önce nesneyi oluşturup daha sonra alanları setter metotlarıyla atamak için eklenmiştir.
    /*Dört parametreyle tüm alanları baştan atar.
      tokenTur: Bu token’ın türünü belirtir (enum Tur).
      icerik: Token’ın kaynak koddaki ham metni.
      baslangicIndex: Token’ın kaynak kod içindeki başlangıç indeksini belirler.
      bitisIndex: Token’ın kaynak kod içindeki bitiş indeksini belirler. */
    public Token(Tur tokenTur, String icerik, int baslangicIndex, int bitisIndex) {
        this.tokenTur = tokenTur;
        this.icerik = icerik;
        this.baslangicIndex = baslangicIndex;
        this.bitisIndex = bitisIndex;
    }
    // Getter Metotlar
    public Tur tokenTurAl() {
        return tokenTur; // Token nesnesinin türünü dön
    }
    public String icerikAl() {
        return icerik; // Token’ın kaynaktaki metin içeriğini dön
    }
    public int baslangicIndexAl() {
        return baslangicIndex; // Token’ın kaynak metin içindeki başlangıç indeksini dön
    }
    public int bitisIndexAl() {
        return bitisIndex; // Token’ın kaynak metin içindeki bitiş indeksini dön
    }
    //Setter Metotlar
    public void tokenTurAta(Tur tokenTur) {
        this.tokenTur = tokenTur; // Token’ın türünü değiştirmeye izin ver
    }
    public void icerikAta(String icerik) {
        this.icerik = icerik; // Token’ın ham metin içeriğini değiştirmeye izin ver
    }
    public void baslangicIndexAta(int baslangicIndex) {
        this.baslangicIndex = baslangicIndex; // Token’ın kaynak metindeki başlangıç indeksini değiştirmeye izin ver
    }
    public void bitisIndexAta(int bitisIndex) {
        this.bitisIndex = bitisIndex; // Token’ın kaynak metindeki bitiş indeksini değiştirmeye izin ver
    }
    //Yardımcı Metotlar
    @Override //toString metodu, Token nesnesinin anlaşılır bir biçimde temsil edilmesini sağlar.
    public String toString() {
        return "[" + tokenTur + ":\"" + icerik + "\"(" + baslangicIndex + "-" + bitisIndex + ")]";
    }
    @Override // equals metodu, iki Token nesnesinin eşitliğini karşılaştırır.
    public boolean equals(Object o) {
        if (this == o) return true;  // Aynı referans mı?
        if (o == null || getClass() != o.getClass())
            return false; // Tip uyumsuzluğu
        Token token = (Token) o;
        return baslangicIndex == token.baslangicIndex &&
                bitisIndex == token.bitisIndex &&
                tokenTur == token.tokenTur &&
                Objects.equals(icerik, token.icerik);
    }
    @Override
    public int hashCode() { // hashCode metodu, equals ile tutarlı olacak şekilde hash kodu üretir.
        return Objects.hash(tokenTur, icerik, baslangicIndex, bitisIndex);
    }}
