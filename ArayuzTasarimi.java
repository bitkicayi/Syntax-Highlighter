import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
/* ArayuzTasarimi sınıfı, kullanıcıdan kod girişi alır ve GUI bileşenleri üzerinden lexer, parser ve renklendirme işlemlerini görsel olarak yönetir.
   Kullanıcı etkileşimli olarak lexical analizi ve parse tree çıktısını butonlar aracılığıyla görebilir. */
public class ArayuzTasarimi extends JFrame {
    private final JTextPane kodPane; // Kod editörü
    private final Lexer lexer; // Lexer örneği
    private final Renklendirme renklendirme; // Renklendirme örneği
    public ArayuzTasarimi() {
        setTitle("Basit Java IDE - Örnek Programlı");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLexical = new JButton("Lexical Analizi Göster");
        JButton btnParserTree = new JButton("Parser Ağacını Göster");
        JButton btnTemizle = new JButton("Temizle");
        butonPanel.add(btnLexical);
        butonPanel.add(btnParserTree);
        butonPanel.add(btnTemizle);
        getContentPane().add(butonPanel, BorderLayout.NORTH);
        kodPane = new JTextPane();
        kodPane.getInputMap().put(KeyStroke.getKeyStroke("control A"), "select-all");
        kodPane.getActionMap().put("select-all", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kodPane.selectAll();
            }
        });
        btnTemizle.addActionListener(e -> {
            kodPane.setText(""); // Metin alanını temizle
        });
        kodPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        lexer = new Lexer();
        renklendirme = new Renklendirme();
        // Örnek Java programı
        String ornekKod =
                "// Tek satirli yorum ornegi\n" +
                        "/*\n" +
                        "   Cok satirli yorum\n" +
                        "   Ornek programin her kösesini test edecek\n" +
                        "*/\n" +
                        "\n" +
                        "package com.ornek;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "\n" +
                        "public class OrnekTest {\n" +
                        "    // Sınıf seviyesi statik degisken\n" +
                        "    private static final double PI = 3.1415926535;\n" +
                        "    \n" +
                        "    public static void main(String[] args) {\n" +
                        "        // Basit degisken atamalari\n" +
                        "        int sayi = 42;\n" +
                        "        float oran = 5.5;\n" +
                        "        boolean a = true;\n" +
                        "        char harf = 'C';\n" +
                        "        String metin = \"Merhaba\\nDunya!\";\n" +
                        "\n" +
                        "        System.out.println(metin);\n" +
                        "\n" +
                        "        // If-else ve iliskisel operatorler\n" +
                        "        if (sayi > 0 && oran < PI) {\n" +
                        "            System.out.println(\"Pozitif sayi ve oran kucuktur PI.\");\n" +
                        "        } else if (!a) {\n" +
                        "            System.out.println(\"a false.\");\n" +
                        "        } else {\n" +
                        "            System.out.println(\"Baska bir durum.\");\n" +
                        "        }\n" +
                        "\n" +
                        "        // Switch-case ornegi\n" +
                        "        int gun = 3;\n" +
                        "        switch (gun) {\n" +
                        "            case 1:\n" +
                        "                System.out.println(\"Pazartesi\");\n" +
                        "                break;\n" +
                        "            case 2:\n" +
                        "                System.out.println(\"Sali\");\n" +
                        "                break;\n" +
                        "            case 3:\n" +
                        "                System.out.println(\"Carsamba\");\n" +
                        "                break;\n" +
                        "            default:\n" +
                        "                System.out.println(\"Diger gun\");\n" +
                        "        }\n" +
                        "\n" +
                        "        // For dongusu ve dizi kullanimi\n" +
                        "        int[] dizi = {1, 2, 3, 4, 5};\n" +
                        "        for (int i = 0; i < dizi.length; i++) {\n" +
                        "            System.out.print(dizi[i] + \", \");\n" +
                        "        }\n" +
                        "        System.out.println();\n" +
                        "\n" +
                        "        // While ve Do-While dongusu\n" +
                        "        int count = 3;\n" +
                        "        while (count > 0) {\n" +
                        "            System.out.println(\"Count: \" + count);\n" +
                        "            count--;\n" +
                        "        }\n" +
                        "\n" +
                        "        do {\n" +
                        "            System.out.println(\"Do-While calisti\");\n" +
                        "        } while (false);\n" +
                        "\n" +
                        "        // Try-Catch-Finally blogu\n" +
                        "        try {\n" +
                        "            int bolum = sayi / 0; // hata oluşturur\n" +
                        "            System.out.println(\"Bölüm: \" + bolum);\n" +
                        "        } catch (ArithmeticException ex) {\n" +
                        "            System.out.println(\"Hata: Sifira bolme hatasi.\");\n" +
                        "        } finally {\n" +
                        "            System.out.println(\"Try-Catch blogu tamamlandi.\");\n" +
                        "        }\n" +
                        "\n" +
                        "        // Metod cagrisi\n" +
                        "        int faktoriyel = faktoriyel(5);\n" +
                        "        System.out.println(\"5! = \" + faktoriyel);\n" +
                        "    }\n" +
                        "\n" +
                        "    // Basit bir faktoriyel metodu (recursive)\n" +
                        "    public static int faktoriyel(int n) {\n" +
                        "        if (n <= 1) {\n" +
                        "            return 1;\n" +
                        "        }\n" +
                        "        return n * faktoriyel(n - 1);\n" +
                        "    }\n" +
                        "}\n";
        kodPane.setText(ornekKod);
        // İlk açılışta renklendir ve AST oluştur
        SwingUtilities.invokeLater(this::yenidenRenklendirVeParseEt);
        // Her tuş bırakıldığında renklendir ve AST oluştur
        kodPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(ArayuzTasarimi.this::yenidenRenklendirVeParseEt);
            }
        });
        JScrollPane scrollKod = new JScrollPane(kodPane);
        getContentPane().add(scrollKod, BorderLayout.CENTER);
        // Lexical Analizi Göster
        btnLexical.addActionListener(e -> {
            String kod = kodPane.getText();
            List<Token> tokenlar = lexer.parcalama(kod);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Kaynak Kod");
            for (Token token : tokenlar) {
                String etiket = tokenEtiketiOlustur(token);
                root.add(new DefaultMutableTreeNode(etiket));
            }
            JFrame lexicalFrame = new JFrame("Lexical Analizi");
            lexicalFrame.setSize(400, 600);
            lexicalFrame.setLocationRelativeTo(this);
            JTree tree = new JTree(root);
            tree.setFont(new Font("Consolas", Font.PLAIN, 12));
            lexicalFrame.add(new JScrollPane(tree));
            lexicalFrame.setVisible(true);
        });
        // Parser Ağacını Gösterir
        btnParserTree.addActionListener(e -> {
            String kod = kodPane.getText();
            List<Token> tokenlar = lexer.parcalama(kod);
            Parser parser = new Parser(tokenlar);
            DefaultMutableTreeNode root = parser.parseAsAgac();
            JFrame treeFrame = new JFrame("Parser Ağacı");
            treeFrame.setSize(400, 650);
            treeFrame.setLocationRelativeTo(this);
            JTree tree = new JTree(root);
            tree.setFont(new Font("Consolas", Font.PLAIN, 12));
            treeFrame.add(new JScrollPane(tree));
            treeFrame.setVisible(true);
        });
    }
    private void traverseAST(DefaultMutableTreeNode node, List<String> liste) { //AST kökünden başlayarak pre order ile tüm düğüm etiketlerini listeye ekle
        liste.add(node.getUserObject().toString());
        for (int i = 0; i < node.getChildCount(); i++) {
            traverseAST((DefaultMutableTreeNode) node.getChildAt(i), liste);
        }}
    private String tokenEtiketiOlustur(Token token) { // Token nesnesine göre "Kategori: İçerik" formatında etiket dön
        Token.Tur tur = token.tokenTurAl();
        String icerik = token.icerikAl();
        switch (tur) {
            case TEK_SATIR_YORUM:
            case COK_SATIR_YORUM:
                return "Yorum: " + icerik;
            case ANAHTAR_SOZCUK_PACKAGE:
            case ANAHTAR_SOZCUK_IMPORT:
            case ANAHTAR_SOZCUK_CLASS:
            case ANAHTAR_SOZCUK_INTERFACE:
            case ANAHTAR_SOZCUK_EXTENDS:
            case ANAHTAR_SOZCUK_IMPLEMENTS:
            case ANAHTAR_SOZCUK_PUBLIC:
            case ANAHTAR_SOZCUK_PRIVATE:
            case ANAHTAR_SOZCUK_PROTECTED:
            case ANAHTAR_SOZCUK_STATIC:
            case ANAHTAR_SOZCUK_FINAL:
            case ANAHTAR_SOZCUK_VOID:
            case ANAHTAR_SOZCUK_INT:
            case ANAHTAR_SOZCUK_BOOLEAN:
            case ANAHTAR_SOZCUK_CHAR:
            case ANAHTAR_SOZCUK_DOUBLE:
            case ANAHTAR_SOZCUK_FLOAT:
            case ANAHTAR_SOZCUK_LONG:
            case ANAHTAR_SOZCUK_SHORT:
            case ANAHTAR_SOZCUK_BYTE:
            case ANAHTAR_SOZCUK_IF:
            case ANAHTAR_SOZCUK_ELSE:
            case ANAHTAR_SOZCUK_SWITCH:
            case ANAHTAR_SOZCUK_CASE:
            case ANAHTAR_SOZCUK_DEFAULT:
            case ANAHTAR_SOZCUK_FOR:
            case ANAHTAR_SOZCUK_WHILE:
            case ANAHTAR_SOZCUK_DO:
            case ANAHTAR_SOZCUK_BREAK:
            case ANAHTAR_SOZCUK_CONTINUE:
            case ANAHTAR_SOZCUK_RETURN:
            case ANAHTAR_SOZCUK_NEW:
            case ANAHTAR_SOZCUK_THIS:
            case ANAHTAR_SOZCUK_SUPER:
            case ANAHTAR_SOZCUK_THROW:
            case ANAHTAR_SOZCUK_THROWS:
            case ANAHTAR_SOZCUK_TRY:
            case ANAHTAR_SOZCUK_CATCH:
            case ANAHTAR_SOZCUK_FINALLY:
            case ANAHTAR_SOZCUK_SYNCHRONIZED:
            case ANAHTAR_SOZCUK_VOLATILE:
            case ANAHTAR_SOZCUK_TRANSIENT:
            case ANAHTAR_SOZCUK_ENUM:
            case ANAHTAR_SOZCUK_ASSERT:
            case ANAHTAR_SOZCUK_INSTANCEOF:
                return "Anahtar Kelime: " + icerik;
            case STRING_DIZI_SABIT:
                return "Metin: " + icerik;
            case KARAKTER_SABIT:
                return "Karakter: " + icerik;
            case TAM_SAYI_SABIT:
            case ONDALIK_SAYI_SABIT:
                return "Sayı: " + icerik;
            case BOOLEAN_SABIT:
                return "Boolean: " + icerik;
            case NULL_SABIT:
                return "Null: " + icerik;
            case TANIMLAYICI:
                return "Tanımlayıcı: " + icerik;
            case ARTIRMA_OPERATOR:
            case AZALTMA_OPERATOR:
            case ESITMI_OPERATOR:
            case NOT_ESITMI_OPERATOR:
            case NOT_OPERATOR:
            case KUCUK_ESITTIR:
            case BUYUK_ESITTIR:
            case KUCUKTUR_OP:
            case BUYUKTUR_OP:
            case VE_OPERATOR:
            case VEYA_OPERATOR:
            case SOL_KAYDIRMA:
            case SAG_KAYDIRMA:
            case UZUN_SAG_KAYDIRMA:
            case BITWISE_AND:
            case BITWISE_OR:
            case BITWISE_XOR:
            case ARTI_OP:
            case EKSI_OP:
            case CARPIM_OP:
            case BOLME_OP:
            case MOD_OP:
            case ATAMA_OPERATOR:
                return "Operatör: " + icerik;
            case NOKTALI_VIRGUL:
            case VIRGUL:
            case AC_PARANTEZ:
            case KAPA_PARANTEZ:
            case AC_SUSLU_PARANTEZ:
            case KAPA_SUSLU_PARANTEZ:
            case AC_KOSE_PARANTEZ:
            case KAPA_KOSE_PARANTEZ:
            case NOKTA:
            case UC_NOKTA:
                return "Ayırıcı: " + icerik;
            case BOSLUK:
                return "Boşluk: [spc]";
            case YENI_SATIR:
                return "Yeni Satır: [\\n]";
            case SEKME:
                return "Sekme: [\\t]";
            case DIGER:
            default:
                return "Diğer: " + icerik;
        }}
    private void yenidenRenklendirVeParseEt() {
        int caretPos = kodPane.getCaretPosition(); // İmleç pozisyonunu al
        String kod = kodPane.getText();
        List<Token> tokenlar = lexer.parcalama(kod);
        StyledDocument doc = kodPane.getStyledDocument();
        try {
            // Belgeyi temizle
            doc.remove(0, doc.getLength());
            // Tüm kodu ekle
            doc.insertString(0, kod, null);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        // Token’lara karşılık gelen renkleri al ve uygula
        List<Renklendirme.ColorSpan> spans = renklendirme.renklendir(tokenlar);
        for (Renklendirme.ColorSpan span : spans) {
            doc.setCharacterAttributes(
                    span.getBaslangic(),
                    span.getUzunluk(),
                    span.getStil(),
                    true
            );
        }
        kodPane.setCaretPosition(Math.min(caretPos, kodPane.getDocument().getLength()));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArayuzTasarimi pencere = new ArayuzTasarimi();
            pencere.setVisible(true);
        });
    }}