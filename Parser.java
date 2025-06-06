import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
/* Parser sınıfı, Lexer’dan aldığı List<Token>’ı baştan sona tarayıp kaynak kodun yapısal (syntax) öğelerini (sınıf tanımları, metod tanımları,
değişken bildirimleri, return ifadeleri, kontrol akışları vb.) temsil edecek bir AST (Abstract Syntax Tree) oluşturur. */
public class Parser {
    private final List<Token> tokenListesi; // Lexer’dan gelen token listesi
    private int currentIndex; // Şu anda hangi token üzerinde olduğumuzu gösterir
    public Parser(List<Token> tokenListesi) {
        this.tokenListesi = tokenListesi;
        this.currentIndex = 0;
    }
    public DefaultMutableTreeNode parseAsAgac() { // Tüm token listesini tarayan fonksiyon
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Program");
        currentIndex = 0; // Başlangıçta en başa dön
        while (!eof()) {
            // Sınıf bildirimi varsa
            DefaultMutableTreeNode sinifNode = parseSinifAsNode();
            if (sinifNode != null) {
                root.add(sinifNode);
                continue;
            }
            // Metod bildirimi varsa
            DefaultMutableTreeNode metodNode = parseMetotAsNode();
            if (metodNode != null) {
                root.add(metodNode);
                continue;
            }
            // Değişken ataması
            DefaultMutableTreeNode degNode = parseDegiskenTanimiAsNode();
            if (degNode != null) {
                root.add(degNode);
                continue;
            }
            // Return ifadesi
            DefaultMutableTreeNode retNode = parseReturnAsNode();
            if (retNode != null) {
                root.add(retNode);
                continue;
            }
            // If Bloğu
            DefaultMutableTreeNode ifNode = parseIfAsNode();
            if (ifNode != null) {
                root.add(ifNode);
                continue;
            }
            // Döngüler (for, while, do-while)
            DefaultMutableTreeNode loopNode = parseDonguAsNode();
            if (loopNode != null) {
                root.add(loopNode);
                continue;
            }
            // Ekrana basma, break/continue gibi basit ifadeler varsa
            DefaultMutableTreeNode simpleNode = parseBasitIfadeAsNode();
            if (simpleNode != null) {
                root.add(simpleNode);
                continue;
            }
            currentIndex++; // Hiçbirine uymadıysa bir sonraki token’a geç
        }
        return root;
    }
    // ---- Sınıf Bildirimi ----
    private DefaultMutableTreeNode parseSinifAsNode() {
        int basla = currentIndex;
        // class anahtar kelimesi yoksa
        if (!kontrolVeIlerle(Token.Tur.ANAHTAR_SOZCUK_CLASS)) {
            return null; // parse'ı iptal et
        }
        // Sonraki token TANIMLAYICI değilse
        if (!kontrolVeIlerle(Token.Tur.TANIMLAYICI)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        String sinifAdi = tokenListesi.get(currentIndex - 1).icerikAl();
        // { yoksa
        if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        DefaultMutableTreeNode sinifNode = new DefaultMutableTreeNode("Sinif: " + sinifAdi);
        int acilisSayaci = 1;
        // } bulana kadar içinde parse et
        while (!eof() && acilisSayaci > 0) {
            // İç içe sınıf, metod, değişken, if, döngü olabilir
            if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                acilisSayaci++;
                currentIndex++;
                continue;
            }
            if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                acilisSayaci--;
                currentIndex++;
                continue;
            }
            DefaultMutableTreeNode altMetod = parseMetotAsNode();
            if (altMetod != null) {
                sinifNode.add(altMetod);
                continue;
            }
            DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
            if (altDeg != null) {
                sinifNode.add(altDeg);
                continue;
            }
            DefaultMutableTreeNode altIf = parseIfAsNode();
            if (altIf != null) {
                sinifNode.add(altIf);
                continue;
            }
            DefaultMutableTreeNode altLoop = parseDonguAsNode();
            if (altLoop != null) {
                sinifNode.add(altLoop);
                continue;
            }
            // Diğer basit ifadeler (örneğin return, break, continue)
            DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
            if (altSimple != null) {
                sinifNode.add(altSimple);
                continue;
            }
            currentIndex++; // Hiçbirine uymadıysa atla
        }
        return sinifNode;
    }
    // ---- Metod Bildirimi ----
    private DefaultMutableTreeNode parseMetotAsNode() {
        int basla = currentIndex;
        // public/private/protected
        if (bakAny(Token.Tur.ANAHTAR_SOZCUK_PUBLIC, Token.Tur.ANAHTAR_SOZCUK_PRIVATE, Token.Tur.ANAHTAR_SOZCUK_PROTECTED)) {
            currentIndex++;
        }
        // static/final
        if (bakAny(Token.Tur.ANAHTAR_SOZCUK_STATIC, Token.Tur.ANAHTAR_SOZCUK_FINAL)) {
            currentIndex++;
        }
        // Dönüş tipi: void, primitive ya da TANIMLAYICI
        if (bak(Token.Tur.ANAHTAR_SOZCUK_VOID) || bak(Token.Tur.ANAHTAR_SOZCUK_INT) || bak(Token.Tur.ANAHTAR_SOZCUK_BOOLEAN) ||
                bak(Token.Tur.ANAHTAR_SOZCUK_CHAR) || bak(Token.Tur.ANAHTAR_SOZCUK_DOUBLE) || bak(Token.Tur.ANAHTAR_SOZCUK_FLOAT) || bak(Token.Tur.ANAHTAR_SOZCUK_LONG) ||
                bak(Token.Tur.ANAHTAR_SOZCUK_SHORT) || bak(Token.Tur.ANAHTAR_SOZCUK_BYTE) || bak(Token.Tur.TANIMLAYICI)
        )
        {
            String donusTipi = tokenListesi.get(currentIndex).icerikAl();
            currentIndex++;
        }
        else {
            return null;
        }
        // Metod adı yoksa
        if (!kontrolVeIlerle(Token.Tur.TANIMLAYICI)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        String metodAdi = tokenListesi.get(currentIndex - 1).icerikAl();
        // ( yoksa
        if (!kontrolVeIlerle(Token.Tur.AC_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // Parametreleri atla ) gelene kadar
        while (!eof() && !bak(Token.Tur.KAPA_PARANTEZ)) {
            // Parametre tipi + isim varsa, bunları atla
            if (bak(Token.Tur.ANAHTAR_SOZCUK_INT) || bak(Token.Tur.ANAHTAR_SOZCUK_BOOLEAN) || bak(Token.Tur.ANAHTAR_SOZCUK_CHAR) ||
                    bak(Token.Tur.ANAHTAR_SOZCUK_DOUBLE) || bak(Token.Tur.ANAHTAR_SOZCUK_FLOAT) || bak(Token.Tur.ANAHTAR_SOZCUK_LONG) ||
                    bak(Token.Tur.ANAHTAR_SOZCUK_SHORT) || bak(Token.Tur.ANAHTAR_SOZCUK_BYTE) || bak(Token.Tur.TANIMLAYICI)) {
                currentIndex++;
                // eğer parametre ismi gelmediyse
                if (!kontrolVeIlerle(Token.Tur.TANIMLAYICI)) {
                    currentIndex = basla;
                    return null; // parse'ı iptal et
                }}
            if (bak(Token.Tur.VIRGUL)) { // Virgül varsa atla
                currentIndex++;
                continue;
            }
            currentIndex++; // Diğer token’ları da atla
        }
        // ) yoksa
        if (!kontrolVeIlerle(Token.Tur.KAPA_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // { yoksa
        if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        DefaultMutableTreeNode metodNode = new DefaultMutableTreeNode("Metot: " + metodAdi);
        DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode("Block");
        int acilisSayaci = 1;
        while (!eof() && acilisSayaci > 0) { // } gelene kadar bloğun içini tarayıp alt yapıları ekle
            if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                acilisSayaci++;
                currentIndex++;
                continue;
            }
            if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                acilisSayaci--;
                currentIndex++;
                continue;
            }
            // Metod içinde: değişken bildirimi
            DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
            if (altDeg != null) {
                blockNode.add(altDeg);
                continue;
            }
            // Metod içinde: return ifadesi
            DefaultMutableTreeNode altRet = parseReturnAsNode();
            if (altRet != null) {
                blockNode.add(altRet);
                continue;
            }
            // Metod içinde: if bloğu
            DefaultMutableTreeNode altIf = parseIfAsNode();
            if (altIf != null) {
                blockNode.add(altIf);
                continue;
            }
            // Metod içinde: döngü
            DefaultMutableTreeNode altLoop = parseDonguAsNode();
            if (altLoop != null) {
                blockNode.add(altLoop);
                continue;
            }
            // Metod içinde: basit ifade (break/continue/print vs.)
            DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
            if (altSimple != null) {
                blockNode.add(altSimple);
                continue;
            }
            currentIndex++; // Diğer token’ları atla
        }
        metodNode.add(blockNode); // Blok düğümünü metoda ekle
        return metodNode;
    }
    // ----Değişken Tanımı ----
    private DefaultMutableTreeNode parseDegiskenTanimiAsNode() {
        int basla = currentIndex;
        // Tür veya önceden tanımlı sınıflar kontrolü
        Token.Tur turIlki = tokenListesi.get(currentIndex).tokenTurAl();
        String tipMetin;
        if (turIlki == Token.Tur.ANAHTAR_SOZCUK_INT || turIlki == Token.Tur.ANAHTAR_SOZCUK_BOOLEAN || turIlki == Token.Tur.ANAHTAR_SOZCUK_CHAR ||
                turIlki == Token.Tur.ANAHTAR_SOZCUK_DOUBLE || turIlki == Token.Tur.ANAHTAR_SOZCUK_FLOAT || turIlki == Token.Tur.ANAHTAR_SOZCUK_LONG ||
                turIlki == Token.Tur.ANAHTAR_SOZCUK_SHORT || turIlki == Token.Tur.ANAHTAR_SOZCUK_BYTE || turIlki == Token.Tur.TANIMLAYICI) {
                    tipMetin = tokenListesi.get(currentIndex).icerikAl();
                    currentIndex++;
        }
        else {
            return null;
        }
        // Değişken adı yoksa
        if (!kontrolVeIlerle(Token.Tur.TANIMLAYICI)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        String degAdi = tokenListesi.get(currentIndex - 1).icerikAl();
        // “Degisken Tanımı” kök düğümü
        DefaultMutableTreeNode degNode = new DefaultMutableTreeNode("Degisken Tanımı");
        // Alt: “Veri Tipi: <tipMetin>”
        DefaultMutableTreeNode tipNode = new DefaultMutableTreeNode("Veri Tipi: " + tipMetin);
        degNode.add(tipNode);
        // Alt: “Tanımlayıcı: <degAdi>”
        DefaultMutableTreeNode adiNode = new DefaultMutableTreeNode("Tanımlayici: " + degAdi);
        degNode.add(adiNode);
        if (bak(Token.Tur.ATAMA_OPERATOR)) {
            currentIndex++;
            // Sabit değeri kontrol et (int, float, string, char, boolean)
            if (!eof() && (bak(Token.Tur.TAM_SAYI_SABIT) || bak(Token.Tur.ONDALIK_SAYI_SABIT) || bak(Token.Tur.STRING_DIZI_SABIT) ||
                            bak(Token.Tur.KARAKTER_SABIT) || bak(Token.Tur.BOOLEAN_SABIT))) {
                String sabitMetin = tokenListesi.get(currentIndex).icerikAl();
                currentIndex++;
                // Alt: “Sabit Değer: <sabitMetin>”
                DefaultMutableTreeNode sabitNode = new DefaultMutableTreeNode("Sabit Deger: " + sabitMetin);
                degNode.add(sabitNode);
            }
            // Atama sonrası ; gelene kadar atla
            while (!eof() && !bak(Token.Tur.NOKTALI_VIRGUL)) {
                currentIndex++;
            }
        }
        // ; yoksa
        if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        return degNode;
    }
    // ---- return ----
    private DefaultMutableTreeNode parseReturnAsNode() {
        int basla = currentIndex;
        // return
        if (!kontrolVeIlerle(Token.Tur.ANAHTAR_SOZCUK_RETURN)) {
            return null;
        }
        // Bir sabit bekler (int, float, string, char, boolean, null)
        // Bir sabit veya tanımlayıcı bekler (int, float, string, char, boolean, null, değişken adı)
        if (eof() || !(bak(Token.Tur.TAM_SAYI_SABIT) || bak(Token.Tur.ONDALIK_SAYI_SABIT) || bak(Token.Tur.STRING_DIZI_SABIT) ||
                bak(Token.Tur.KARAKTER_SABIT) || bak(Token.Tur.BOOLEAN_SABIT) || bak(Token.Tur.NULL_SABIT) || bak(Token.Tur.TANIMLAYICI)))
        {
            currentIndex = basla;
            return null;
        }

        String sabitMetin = tokenListesi.get(currentIndex).icerikAl();
        currentIndex++;
        if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // Return düğümü
        DefaultMutableTreeNode returnNode = new DefaultMutableTreeNode("Return İfadesi");
        DefaultMutableTreeNode sabitNode = new DefaultMutableTreeNode("Dönen Değer: " + sabitMetin);
        returnNode.add(sabitNode);
        return returnNode;
    }
    // ---- if ----
    private DefaultMutableTreeNode parseIfAsNode() {
        int basla = currentIndex;
        // if yoksa
        if (!kontrolVeIlerle(Token.Tur.ANAHTAR_SOZCUK_IF)) {
            return null; // parse'ı iptal et
        }
        // ( yoksa
        if (!kontrolVeIlerle(Token.Tur.AC_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // Koşul stringini topla
        StringBuilder kosulBuilder = new StringBuilder();
        int kosulBaslangic = currentIndex;
        while (!eof() && !bak(Token.Tur.KAPA_PARANTEZ)) {
            kosulBuilder.append(tokenListesi.get(currentIndex).icerikAl()).append(" ");
            currentIndex++;
        }
        // ) yoksa
        if (!kontrolVeIlerle(Token.Tur.KAPA_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // { yoksa
        if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
            currentIndex = basla;
            return null; // parse'ı iptal et
        }
        // If bloğu kök düğümü
        DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("If Bloğu");
        // Condition düğümü ekle
        DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Koşul: " + kosulBuilder.toString().trim());
        ifNode.add(conditionNode);
        // Block düğümü oluştur
        DefaultMutableTreeNode ifBlockNode = new DefaultMutableTreeNode("Blok");
        int acilisSayaci = 1;
        // Süslü parantez dengesi bitene kadar içeriği tara
        while (!eof() && acilisSayaci > 0) {
            if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                acilisSayaci++;
                currentIndex++;
                continue;
            }
            if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                acilisSayaci--;
                currentIndex++;
                continue;
            }
            // If içinde: değişken bildirimi
            DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
            if (altDeg != null) {
                ifBlockNode.add(altDeg);
                continue;
            }
            // If içinde: return ifadesi
            DefaultMutableTreeNode altRet = parseReturnAsNode();
            if (altRet != null) {
                ifBlockNode.add(altRet);
                continue;
            }
            // If içinde: başka bir if (nested)
            DefaultMutableTreeNode altIf = parseIfAsNode();
            if (altIf != null) {
                ifBlockNode.add(altIf);
                continue;
            }
            // If içinde: döngü
            DefaultMutableTreeNode altLoop = parseDonguAsNode();
            if (altLoop != null) {
                ifBlockNode.add(altLoop);
                continue;
            }
            // If içinde: basit ifade
            DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
            if (altSimple != null) {
                ifBlockNode.add(altSimple);
                continue;
            }
            currentIndex++;
        }
        ifNode.add(ifBlockNode);
        if (bak(Token.Tur.ANAHTAR_SOZCUK_ELSE)) {
            currentIndex++;
            // { yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            DefaultMutableTreeNode elseNode = new DefaultMutableTreeNode("Else Bloğu");
            DefaultMutableTreeNode elseBlockNode = new DefaultMutableTreeNode("Blok");
            acilisSayaci = 1;
            // Süslü parantez dengesi bitene kadar devam et
            while (!eof() && acilisSayaci > 0) {
                if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                    acilisSayaci++;
                    currentIndex++;
                    continue;
                }
                if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                    acilisSayaci--;
                    currentIndex++;
                    continue;
                }
                // Else içinde: değişken bildirimi
                DefaultMutableTreeNode altDeg2 = parseDegiskenTanimiAsNode();
                if (altDeg2 != null) {
                    elseBlockNode.add(altDeg2);
                    continue;
                }
                // Else içinde: return ifadesi
                DefaultMutableTreeNode altRet2 = parseReturnAsNode();
                if (altRet2 != null) {
                    elseBlockNode.add(altRet2);
                    continue;
                }
                // Else içinde: nested if
                DefaultMutableTreeNode altIf2 = parseIfAsNode();
                if (altIf2 != null) {
                    elseBlockNode.add(altIf2);
                    continue;
                }
                // Else içinde: döngü
                DefaultMutableTreeNode altLoop2 = parseDonguAsNode();
                if (altLoop2 != null) {
                    elseBlockNode.add(altLoop2);
                    continue;
                }
                // Else içinde: basit ifade
                DefaultMutableTreeNode altSimple2 = parseBasitIfadeAsNode();
                if (altSimple2 != null) {
                    elseBlockNode.add(altSimple2);
                    continue;
                }
                currentIndex++;
            }
            elseNode.add(elseBlockNode);
            ifNode.add(elseNode);
        }
        return ifNode;
    }
    // Döngüler
    private DefaultMutableTreeNode parseDonguAsNode() {
        int basla = currentIndex;
        // ---- for ----
        if (bak(Token.Tur.ANAHTAR_SOZCUK_FOR)) {
            currentIndex++;
            // ( yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // ) gelene kadar atla ve koşul metni topla
            StringBuilder kosulBuilder = new StringBuilder();
            int parantezBaslangic = currentIndex;
            while (!eof() && !bak(Token.Tur.KAPA_PARANTEZ)) {
                kosulBuilder.append(tokenListesi.get(currentIndex).icerikAl()).append(" ");
                currentIndex++;
            }
            // ) yoksa
            if (!kontrolVeIlerle(Token.Tur.KAPA_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // { yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            DefaultMutableTreeNode forNode = new DefaultMutableTreeNode("For Döngüsü");
            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Koşul: " + kosulBuilder.toString().trim());
            forNode.add(conditionNode);
            DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode("Blok");
            int acilisSayaci = 1;
            // İçeriyi tararken alt yapıları ekle
            while (!eof() && acilisSayaci > 0) {
                if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                    acilisSayaci++;
                    currentIndex++;
                    continue;
                }
                if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                    acilisSayaci--;
                    currentIndex++;
                    continue;
                }
                // For içinde: değişken bildirimi
                DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
                if (altDeg != null) {
                    blockNode.add(altDeg);
                    continue;
                }
                // For içinde: return ifadesi
                DefaultMutableTreeNode altRet = parseReturnAsNode();
                if (altRet != null) {
                    blockNode.add(altRet);
                    continue;
                }
                // For içinde: nested if
                DefaultMutableTreeNode altIf = parseIfAsNode();
                if (altIf != null) {
                    blockNode.add(altIf);
                    continue;
                }
                // For içinde: nested döngü
                DefaultMutableTreeNode altLoop = parseDonguAsNode();
                if (altLoop != null) {
                    blockNode.add(altLoop);
                    continue;
                }
                // For içinde: basit ifade
                DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
                if (altSimple != null) {
                    blockNode.add(altSimple);
                    continue;
                }
                currentIndex++;
            }
            forNode.add(blockNode);
            return forNode;
        }
        // ---- while ----
        if (bak(Token.Tur.ANAHTAR_SOZCUK_WHILE)) {
            currentIndex++;
            // ( yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // ) gelene kadar atla + koşul metni topla
            StringBuilder kosulBuilder = new StringBuilder();
            int parantezBaslangic = currentIndex;
            while (!eof() && !bak(Token.Tur.KAPA_PARANTEZ)) {
                kosulBuilder.append(tokenListesi.get(currentIndex).icerikAl()).append(" ");
                currentIndex++;
            }
            // ) yoksa
            if (!kontrolVeIlerle(Token.Tur.KAPA_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // { yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
                currentIndex = basla; // parse'ı iptal et
                return null;
            }
            DefaultMutableTreeNode whileNode = new DefaultMutableTreeNode("While Döngüsü");
            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Koşul: " + kosulBuilder.toString().trim());
            whileNode.add(conditionNode);
            DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode("Blok");
            int acilisSayaci = 1;
            while (!eof() && acilisSayaci > 0) {
                if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                    acilisSayaci++;
                    currentIndex++;
                    continue;
                }
                if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                    acilisSayaci--;
                    currentIndex++;
                    continue;
                }
                // While içinde: değişken bildirimi
                DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
                if (altDeg != null) {
                    blockNode.add(altDeg);
                    continue;
                }
                // While içinde: return ifadesi
                DefaultMutableTreeNode altRet = parseReturnAsNode();
                if (altRet != null) {
                    blockNode.add(altRet);
                    continue;
                }
                // While içinde: nested if
                DefaultMutableTreeNode altIf = parseIfAsNode();
                if (altIf != null) {
                    blockNode.add(altIf);
                    continue;
                }
                // While içinde: nested döngü
                DefaultMutableTreeNode altLoop = parseDonguAsNode();
                if (altLoop != null) {
                    blockNode.add(altLoop);
                    continue;
                }
                // While içinde: basit ifade
                DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
                if (altSimple != null) {
                    blockNode.add(altSimple);
                    continue;
                }
                currentIndex++;
            }
            whileNode.add(blockNode);
            return whileNode;
        }
        // ---- Do-While ----
        if (bak(Token.Tur.ANAHTAR_SOZCUK_DO)) {
            currentIndex++;
            // { yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_SUSLU_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            DefaultMutableTreeNode doNode = new DefaultMutableTreeNode("Do-While Döngüsü");
            DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode("Block");
            int acilisSayaci = 1;
            // Do bloğu içini tara
            while (!eof() && acilisSayaci > 0) {
                if (bak(Token.Tur.AC_SUSLU_PARANTEZ)) {
                    acilisSayaci++;
                    currentIndex++;
                    continue;
                }
                if (bak(Token.Tur.KAPA_SUSLU_PARANTEZ)) {
                    acilisSayaci--;
                    currentIndex++;
                    continue;
                }
                // Do içinde: değişken bildirimi
                DefaultMutableTreeNode altDeg = parseDegiskenTanimiAsNode();
                if (altDeg != null) {
                    blockNode.add(altDeg);
                    continue;
                }
                // Do içinde: return ifadesi
                DefaultMutableTreeNode altRet = parseReturnAsNode();
                if (altRet != null) {
                    blockNode.add(altRet);
                    continue;
                }
                // Do içinde: nested if
                DefaultMutableTreeNode altIf = parseIfAsNode();
                if (altIf != null) {
                    blockNode.add(altIf);
                    continue;
                }
                // Do içinde: nested döngü
                DefaultMutableTreeNode altLoop = parseDonguAsNode();
                if (altLoop != null) {
                    blockNode.add(altLoop);
                    continue;
                }
                // Do içinde: basit ifade
                DefaultMutableTreeNode altSimple = parseBasitIfadeAsNode();
                if (altSimple != null) {
                    blockNode.add(altSimple);
                    continue;
                }
                currentIndex++;
            }
            doNode.add(blockNode);
            // while yoksa
            if (!kontrolVeIlerle(Token.Tur.ANAHTAR_SOZCUK_WHILE)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // ( yoksa
            if (!kontrolVeIlerle(Token.Tur.AC_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // ) gelene kadar koşul metni topla
            StringBuilder kosulBuilder = new StringBuilder();
            while (!eof() && !bak(Token.Tur.KAPA_PARANTEZ)) {
                kosulBuilder.append(tokenListesi.get(currentIndex).icerikAl()).append(" ");
                currentIndex++;
            }
            // ) yoksa
            if (!kontrolVeIlerle(Token.Tur.KAPA_PARANTEZ)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // ; yoksa
            if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Koşul: " + kosulBuilder.toString().trim());
            doNode.add(conditionNode);
            return doNode;
        }
        return null;
    }
    // Basit İfadeler
    private DefaultMutableTreeNode parseBasitIfadeAsNode() {
        int basla = currentIndex;
        // break
        if (bak(Token.Tur.ANAHTAR_SOZCUK_BREAK)) {
            currentIndex++;
            // ; yoksa
            if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            return new DefaultMutableTreeNode("Break İfadesi");
        }
        // continue
        if (bak(Token.Tur.ANAHTAR_SOZCUK_CONTINUE)) {
            currentIndex++;
            // ; yoksa
            if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            return new DefaultMutableTreeNode("Continue İfadesi");
        }
        if (bak(Token.Tur.TANIMLAYICI) &&
                tokenListesi.get(currentIndex).icerikAl().equals("System")) {
            // “System.out.println( … ) ;” kalıbını tespit edip bir düğüm oluşturur
            DefaultMutableTreeNode printNode = new DefaultMutableTreeNode("Print İfadesi");
            // Parantez içeriğini string olarak topla
            StringBuilder parantezIci = new StringBuilder();
            boolean parantezAcildi = false;
            while (!eof() && !bak(Token.Tur.NOKTALI_VIRGUL)) {
                if (bak(Token.Tur.AC_PARANTEZ)) parantezAcildi = true;
                if (parantezAcildi && !bak(Token.Tur.KAPA_PARANTEZ) && !bak(Token.Tur.AC_PARANTEZ))
                    parantezIci.append(tokenListesi.get(currentIndex).icerikAl()).append(" ");
                currentIndex++;
            }
            // ; yoksa
            if (!kontrolVeIlerle(Token.Tur.NOKTALI_VIRGUL)) {
                currentIndex = basla;
                return null; // parse'ı iptal et
            }
            // Eğer parantez içi varsa alt düğüm ekle
            if (parantezIci.length() > 0) {
                printNode.add(new DefaultMutableTreeNode("PrintArg: " + parantezIci.toString().trim()));
            }
            return printNode;
        }
        return null;
    }
    // Bir sonraki token’ın türü “tur” ise true dön.
    private boolean bak(Token.Tur tur) {
        return !eof() && tokenListesi.get(currentIndex).tokenTurAl() == tur;
    }
    // Eğer currentIndex’teki token türü “tur” ise indexi bir sonraki tokena atlat ve true dön
    private boolean kontrolVeIlerle(Token.Tur tur) {
        if (bak(tur)) {
            currentIndex++;
            return true;
        }
        return false;
    }
    // Eğer currentIndex’teki token türü, “turler” dizisinden herhangi birine eşitse true dön
    private boolean bakAny(Token.Tur... turler) {
        if (eof()) return false;
        Token.Tur suAnki = tokenListesi.get(currentIndex).tokenTurAl();
        for (Token.Tur t : turler) {
            if (suAnki == t) {
                return true;
            }}
        return false;
    }
    // Tüm tokenListesi taranıp bitmiş mi kontrolü
    private boolean eof() {
        return currentIndex >= tokenListesi.size();
    }}