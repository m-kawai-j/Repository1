package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Numer0nController {

    private final Numer0nService numeronService;

    // サービスを注入（DI: 依存性の注入）
    public Numer0nController(Numer0nService numeronService) {
        this.numeronService = numeronService;
    }

    // 初回アクセスとリロード時
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "かぶりなしの数字を3つ入力してね\n８回までに当てられたら勝ちだよ！");
        model.addAttribute("attemptCount", numeronService.getAttemptCount());

        // 【修正】テンプレート参照に必要な変数を初期値で追加
        model.addAttribute("result", null); 
        model.addAttribute("lastCall", null);
        model.addAttribute("errorMessage", null);
        model.addAttribute("gameOver", false);
        
        // 【追加】履歴リスト
        model.addAttribute("historyList", numeronService.getHistoryList());

        return "numeron";
    }

    // リセットリクエストを受け付ける
    @GetMapping("/reset")
    public String reset(Model model) {
        numeronService.resetGame();
        model.addAttribute("message", "ゲームをリセットしました。新しい数字をコールしてください。");
        model.addAttribute("attemptCount", numeronService.getAttemptCount());

        // 【修正】テンプレート参照に必要な変数をリセット値で追加
        model.addAttribute("result", null);
        model.addAttribute("lastCall", null);
        model.addAttribute("errorMessage", null);
        model.addAttribute("gameOver", false);
        
        // 【追加】履歴リスト（リセットで空になっている）
        model.addAttribute("historyList", numeronService.getHistoryList());

        return "numeron";
    }

    @PostMapping("/call")
    public String handleCall(
            @RequestParam("n1") int n1,
            @RequestParam("n2") int n2,
            @RequestParam("n3") int n3,
            Model model) {

        String validationError = null;

        if (n1 < 0 || n1 > 9 || n2 < 0 || n2 > 9 || n3 < 0 || n3 > 9) {
            validationError = "❌ 0から9までの数字を入力してください。";
        } else if (n1 == n2 || n1 == n3 || n2 == n3) {
            validationError = "❌ 3つの数字はすべて異なる数字である必要があります。";
        }

        // エラーがあった場合は、ジャッジせずに画面に戻す
        if (validationError != null) {
            model.addAttribute("errorMessage", validationError);
            model.addAttribute("message", "入力内容を確認し、もう一度コールしてください。");
            model.addAttribute("attemptCount", numeronService.getAttemptCount());
            
            // 【修正】エラー時は以前の状態を維持
            model.addAttribute("lastCall", n1 + "," + n2 + "," + n3);
            model.addAttribute("result", null); 
            model.addAttribute("gameOver", false);
            model.addAttribute("historyList", numeronService.getHistoryList());

            return "numeron";
        }

        // ジャッジを実行 (Service内で attemptCount++ と履歴保存が実行される)
        String result = numeronService.judge(n1, n2, n3);

        model.addAttribute("lastCall", n1 + "," + n2 + "," + n3);
        model.addAttribute("result", result);
        model.addAttribute("message", "コールを受け付けました");
        model.addAttribute("attemptCount", numeronService.getAttemptCount());
        model.addAttribute("errorMessage", null);
        
        // 【修正】ジャッジ結果に基づき gameOver フラグを設定
        model.addAttribute("gameOver", numeronService.isGameOver(result));
        
        // 【追加】履歴リスト
        model.addAttribute("historyList", numeronService.getHistoryList());

        return "numeron";
    }
}