package com.coderpwh.plugins;

public class PromptFunctionConstants {


    public static final String SummarizeConversationDefinition = """
            BEGIN CONTENT TO SUMMARIZE:
            {{$INPUT}}

            END CONTENT TO SUMMARIZE.

            请总结 '待总结内容' 中的对话，识别讨论的主要要点以及达成的任何结论。
            请勿引入其他通用知识。
            总结请使用纯文本、完整句子，不要包含任何标记或标签。

            BEGIN SUMMARY:
            """
            .stripIndent();


    public static final String GetConversationActionItemsDefinition = """
            你是一个行动项提取器。你将获得聊天记录，需要记录聊天中提到的行动项。
            如果内容中存在行动项，请将其提取出来。如果没有行动项，则返回空。如果单个字段缺失，请使用空字符串。
            以 JSON 格式返回行动项。

            行动项的可能状态包括：Open（未开始）, Closed（已关闭）, In Progress（进行中）。

            包含行动项的输入示例：

            John Doe 说："我将在周五前录制新功能的演示视频"
            我说："太好了，谢谢 John。我们可能不会全部使用，但发布出来是好的"

            示例输出：
            {
                "actionItems": [
                    {
                        "owner": "John Doe",
                        "actionItem": "录制新功能的演示视频",
                        "dueDate": "周五前",
                        "status": "Open",
                        "notes": ""
                    }
                ]
            }

            不包含行动项的输入示例：

            John Doe 说："嘿，我要去商店，你需要带什么吗？"
            我说："不用了，谢谢。"

            示例输出：
            {
                "action_items": []
            }

            内容开始：

            {{$INPUT}}

            内容结束。

            输出：
            """
            .stripIndent();

    public static final String GetConversationTopicsDefinition = """
        分析以下对话片段，提取关键主题。
        - 仅提取值得记住的主题。
        - 保持简洁。使用短语。
        - 允许使用不完整的英语短语。
        - 简洁性非常重要。
        - 主题可包含需要回忆的记忆名称。
        - 禁止使用长句。使用短语。
        - 以 JSON 格式返回。
        [输入]
        我的名字是麦克白。我过去是苏格兰国王，但已经去世了。我妻子的名字是麦克白夫人，我们结婚15年。我们没有孩子。我们心爱的狗托比·麦克达夫是森林里著名的捕鼠能手。
        莎士比亚在一部戏剧中使我的悲剧故事永垂不朽。
        [输出]
        {
          "topics": [
            "麦克白",
            "苏格兰国王",
            "麦克白夫人",
            "狗",
            "托比·麦克达夫",
            "莎士比亚",
            "戏剧",
            "悲剧"
          ]
        }
        +++++
        [输入]
        {{$INPUT}}
        [输出]"""
            .stripIndent();

}
