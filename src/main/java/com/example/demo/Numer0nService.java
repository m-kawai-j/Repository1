package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Numer0nService {

    // ----------------------------------------------------
    // 【追加】CallHistoryクラス (ネストクラスとして定義)
    // ----------------------------------------------------
    public static class CallHistory {
        private final String call;
        private final String result;

        public CallHistory(String call, String result) {
            this.call = call;
            this.result = result;
        }

        public String getCall() { return call; }
        public String getResult() { return result; }
    }

    // クラスフィールド
    private final int[] answer = new int[3];
    private int attemptCount = 0;
    
    // ----------------------------------------------------
    // 【追加】履歴リスト
    // ----------------------------------------------------
    private List<CallHistory> historyList = new ArrayList<>();

    public Numer0nService() {
        generateAnswer();
    }

    // 履歴リストのGetter
    public List<CallHistory> getHistoryList() {
        return historyList;
    }

    private void generateAnswer() {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digits.add(i);
        }
        Collections.shuffle(digits);

        for (int i = 0; i < 3; i++) {
            this.answer[i] = digits.get(i);
        }
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    /* ゲームをリセットし、新しい答えを生成するメソッド */
    public void resetGame() {
        this.attemptCount = 0;
        generateAnswer();
        // 【修正】リセット時に履歴をクリア
        this.historyList.clear();
    }

    /* プレイヤーのコールを受け取り、ジャッジ結果を返すメソッド */
    public String judge(int num1, int int2, int num3) {
        int[] playerGuess = {num1, int2, num3};
        attemptCount++;

        int eat = 0;
        int bite = 0;

        // ジャッジロジック
        for (int i = 0; i < playerGuess.length; i++) {
            for (int j = 0; j < answer.length; j++) {
                if (i == j && playerGuess[i] == answer[j]) {
                    eat += 1;
                } else if (i != j && playerGuess[i] == answer[j]) {
                    bite += 1;
                }
            }
        }

        // 判定と結果の返却
        String resultString;
        if (eat == 3) {
            resultString = "3EAT-0BITE: あたり！！ (" + attemptCount + "回目)";
        } else if (attemptCount >= 8) {
            resultString = eat + "EAT - " + bite + "BITE. 残念、8回で当てられませんでした。\n正解は " + answer[0] + answer[1] + answer[2] + " でした。";
        } else {
            resultString = eat + "EAT - " + bite + "BITE";
        }

        // 【追加】履歴を保存
        String call = "" + num1 + int2 + num3;
        historyList.add(new CallHistory(call, resultString));

        return resultString;
    }

    /* ゲームが終了しているかどうかを判定する */
    public boolean isGameOver(String resultString) {
        // 3EAT判定または回数オーバーで終了
        return resultString.contains("3EAT-0BITE") || this.attemptCount >= 8;
    }
}