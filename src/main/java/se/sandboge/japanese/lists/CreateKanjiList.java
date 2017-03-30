package se.sandboge.japanese.lists;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class CreateKanjiList {
    private String strings[] = { "お兄さん",
        "お姉さん",
        "お手洗い",
        "どんな所",
        "ゼロ度",
        "一回",
        "一度",
        "不便",
        "世界",
        "世話",
        "世話する",
        "中止",
        "主人",
        "京都",
        "今度",
        "仕事",
        "代わりに",
        "以上",
        "以下",
        "以外",
        "会場",
        "会社員",
        "低い",
        "住む",
        "住所",
        "何回",
        "作者",
        "使う",
        "借りる",
        "働く",
        "兄",
        "兄弟",
        "光",
        "光る",
        "入学式",
        "出発",
        "出発する",
        "別に",
        "別の",
        "別れる",
        "動く",
        "北区",
        "医学",
        "医者",
        "台所",
        "台風",
        "合う",
        "同じ",
        "品物",
        "問題",
        "回る",
        "図書館",
        "地下",
        "地図",
        "地理",
        "場合",
        "場所",
        "声",
        "売り場",
        "大使館",
        "太い",
        "太る",
        "夫",
        "妹",
        "妻",
        "姉",
        "始まる",
        "始める",
        "字",
        "学者",
        "家",
        "家族",
        "宿題",
        "寒い",
        "小説",
        "屋上",
        "工場",
        "工業",
        "市民",
        "店員",
        "建てる",
        "建物",
        "引き出し",
        "引く",
        "弟",
        "弱い",
        "思い出",
        "思い出す",
        "思う",
        "急ぐ",
        "急に",
        "急行",
        "悪い",
        "意味",
        "意見",
        "戸",
        "手紙",
        "押す",
        "教える",
        "教会",
        "教室",
        "文化",
        "文学",
        "旅行",
        "旅館",
        "昔",
        "映画館",
        "時代",
        "時計",
        "暑い",
        "暗い",
        "有名",
        "本屋",
        "東京",
        "東京都",
        "止まる",
        "止める",
        "正しい",
        "正午",
        "正式",
        "正月",
        "歩く",
        "死ぬ",
        "池",
        "注意",
        "注意する",
        "洗う",
        "漢字",
        "火事",
        "物",
        "特に",
        "特別",
        "生産",
        "生産する",
        "産業",
        "用事",
        "用意",
        "用意する",
        "留学する",
        "留学生",
        "発音",
        "着物",
        "知らせる",
        "知る",
        "短い",
        "石",
        "研究",
        "研究室",
        "研究者",
        "私",
        "竹",
        "答える",
        "糸",
        "紙",
        "終わり",
        "終わる",
        "羽",
        "習う",
        "考え",
        "考える",
        "自動車",
        "英語",
        "薬",
        "親",
        "親切",
        "計画",
        "計画する",
        "試合",
        "試験",
        "説明",
        "豆",
        "買い物",
        "貸す",
        "質問",
        "軽い",
        "近い",
        "近く",
        "近所",
        "送る",
        "通う",
        "通り",
        "通る",
        "進む",
        "運ぶ",
        "運動する",
        "運転する",
        "運転手",
        "遠い",
        "遠く",
        "都合",
        "重い",
        "銀行員",
        "長官",
        "長野県",
        "閉まる",
        "閉める",
        "開く",
        "間に合う",
        "集める",
        "集る",
        "電気代",
        "電池",
        "頭",
        "顔",
        "風",
        "食べ物",
        "食事",
        "食事する",
        "食堂",
        "飲み物",
        "首都",
};

    private static Map kanjiMap = new HashMap<String,String>();

    public static void main(String[] args) throws IOException {
        CreateKanjiList jc = new CreateKanjiList();
        jc.populateKanjiMap();
        jc.generateTable();
    }

    private void generateTable() {
        for (Object item :
                kanjiMap.keySet()) {
            System.out.println("{\"\", \"" + item + "\"}");
        }
    }

    private void populateKanjiMap() throws IOException {
        for (String string:
             strings) {
            for (Character kanji :
                    string.toCharArray()) {
                if (kanji >= '\u3400' && kanji <= '\u9faf') {
                    String key = String.valueOf(kanji);
                    if (!kanjiMap.containsKey(key)) {
                        String value = Integer.toHexString(kanji);
                        kanjiMap.put(key, value);
                        System.out.println(key + ":" + value);
                        Path from = Paths.get("/Users/psandboge/Documents/Japanska/kanji/0" + value + ".svg");
                        Path to = Paths.get("/Users/psandboge/Documents/Japanska/used_kanjis/0" + value + ".svg");
                        CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                        };
                        java.nio.file.Files.copy(from, to, options);
                    }
                }
            }
        }
    }

}
