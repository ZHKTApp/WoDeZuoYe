package com.zwyl.myhomework.main.subject;

import java.util.List;

public class BeanSubject {
    /**
     * isSeeAnswer : false
     * exercisesItemList : [{"exercisesTitle":"计算下列内容的结果","exerciseScore":30,"studentScore":"","exerciseAnswer":"","analysisFileUri":"","isMistakesCollection":true,"workResultComment":"","answerFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/42342","exercisesId":"24059406c64e478ba75cb49df74d0bb0","isRight":"","studentAnswerFileUri":"","workContentFileUri":"","exerciseAnalysis":"解析，省略190字","isConfirnExpianation":"","rankNo":1,"exerciseTypeCode":30105},{"exercisesTitle":"下列说法中正确的是","exerciseScore":10,"studentScore":"","exerciseAnswer":"danxuan001","analysisFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/3464645","isMistakesCollection":false,"exerciseOptionList":[{"optionId":"danxuan001","optionNo":"A","rankNo":1,"isRight":true,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"北京"},{"optionId":"danxuan002","optionNo":"B","rankNo":2,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"上海"},{"optionId":"danxuan003","optionNo":"C","rankNo":3,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"广州"}],"workResultComment":"","answerFileUri":"","exercisesId":"8c330e9bb90e4904aeca29aa8e5c6997","isRight":"","studentAnswerFileUri":"","workContentFileUri":"","exerciseAnalysis":"解析这是解析","isConfirnExpianation":"","rankNo":2,"exerciseTypeCode":30101}]
     */

    public boolean isSeeAnswer;
    public boolean isSubmit;
    public List<ExercisesItemListBean> exercisesItemList;
    public String workCommand;
    public String exerciseTotalScore;
    public String studentAnswerFileUri;
    public String workContentFileUri;
    public String workName;
    public String studentTotalScore;
    public static class ExercisesItemListBean {
        /**
         * exercisesTitle : 计算下列内容的结果
         * exerciseScore : 30
         * studentScore :
         * exerciseAnswer :
         * analysisFileUri :
         * isMistakesCollection : true
         * workResultComment :
         * answerFileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/42342
         * exercisesId : 24059406c64e478ba75cb49df74d0bb0
         * isRight :
         * studentAnswerFileUri :
         * workContentFileUri :
         * exerciseAnalysis : 解析，省略190字
         * isConfirnExpianation :
         * rankNo : 1
         * exerciseTypeCode : 30105
         * exerciseOptionList : [{"optionId":"danxuan001","optionNo":"A","rankNo":1,"isRight":true,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"北京"},{"optionId":"danxuan002","optionNo":"B","rankNo":2,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"上海"},{"optionId":"danxuan003","optionNo":"C","rankNo":3,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"广州"}]
         */
        public String studentAnswerOptionId;
        public String exercisesTitle;
        public int exerciseScore;
        public String studentScore;
        public String exerciseAnswer;
        public String analysisFileUri;
        public boolean isMistakesCollection;
        public String workResultComment;
        public String answerFileUri;
        public String exercisesId;
        public String exerciseExplainFileUri;
        public String isRight;
        public String studentAnswerFileUri;
        public String workContentFileUri;
        public String exerciseAnalysis;
        public boolean isConfirnExpianation;
        public int rankNo;
        public int exerciseTypeCode;
        public List<ExerciseOptionListBean> exerciseOptionList;

        public static class ExerciseOptionListBean {
            /**
             * optionId : danxuan001
             * optionNo : A
             * rankNo : 1
             * isRight : true
             * createTime : 2017-06-03 15:59:59
             * createUser : 001
             * lastUpdateTime : 2017-06-03 15:59:59
             * lastUpdateUser : 001
             * isDeleted : false
             * exerciseId : 8c330e9bb90e4904aeca29aa8e5c6997
             * optionContent : 北京
             */

            public String optionId;
            public String optionNo;
            public int rankNo;
            public boolean ischeck;
            public boolean isRight;
            public String createTime;
            public String createUser;
            public String lastUpdateTime;
            public String lastUpdateUser;
            public boolean isDeleted;
            public String exerciseId;
            public String optionContent;
        }

        @Override
        public String toString() {
            return "ExercisesItemListBean{" +
                    "exercisesTitle='" + exercisesTitle + '\'' +
                    ", exerciseScore=" + exerciseScore +
                    ", studentScore='" + studentScore + '\'' +
                    ", exerciseAnswer='" + exerciseAnswer + '\'' +
                    ", analysisFileUri='" + analysisFileUri + '\'' +
                    ", isMistakesCollection=" + isMistakesCollection +
                    ", workResultComment='" + workResultComment + '\'' +
                    ", answerFileUri='" + answerFileUri + '\'' +
                    ", exercisesId='" + exercisesId + '\'' +
                    ", exerciseExplainFileUri='" + exerciseExplainFileUri + '\'' +
                    ", isRight='" + isRight + '\'' +
                    ", studentAnswerFileUri='" + studentAnswerFileUri + '\'' +
                    ", workContentFileUri='" + workContentFileUri + '\'' +
                    ", exerciseAnalysis='" + exerciseAnalysis + '\'' +
                    ", isConfirnExpianation='" + isConfirnExpianation + '\'' +
                    ", rankNo=" + rankNo +
                    ", exerciseTypeCode=" + exerciseTypeCode +
                    '}';
        }
    }


    //    /**
//     * isSeeAnswer : false
//     * exercisesItemList : [{"exercisesTitle":"计算下列内容的结果","exerciseAnswer":"","analysisFileUri":"","isMistakesCollection":true,"studentOptionId":"","workResultComment":"","answerFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/42342","exercisesId":"24059406c64e478ba75cb49df74d0bb0","score":30,"isRight":"","studentAnswerFileUri":"","workContentFileUri":"","exerciseAnalysis":"解析，省略190字","isConfirnExpianation":"","rankNo":1,"exerciseTypeCode":30105},{"exercisesTitle":"下列说法中正确的是","exerciseAnswer":"danxuan001","analysisFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/3464645","isMistakesCollection":false,"exerciseOptionList":[{"optionId":"danxuan001","optionNo":"A","rankNo":1,"isRight":true,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"北京"},{"optionId":"danxuan002","optionNo":"B","rankNo":2,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"上海"},{"optionId":"danxuan003","optionNo":"C","rankNo":3,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"广州"}],"studentOptionId":"","workResultComment":"","answerFileUri":"","exercisesId":"8c330e9bb90e4904aeca29aa8e5c6997","score":10,"isRight":"","studentAnswerFileUri":"","workContentFileUri":"","exerciseAnalysis":"解析这是解析","isConfirnExpianation":"","rankNo":2,"exerciseTypeCode":30101}]
//     */
//
//    public boolean isSeeAnswer;
//    public List<ExercisesItemListBean> exercisesItemList;
//
//    public static class ExercisesItemListBean {
//        /**
//         * exercisesTitle : 计算下列内容的结果
//         * exerciseAnswer :
//         * analysisFileUri :
//         * isMistakesCollection : true
//         * studentOptionId :
//         * workResultComment :
//         * answerFileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/42342
//         * exercisesId : 24059406c64e478ba75cb49df74d0bb0
//         * score : 30
//         * isRight :
//         * studentAnswerFileUri :
//         * workContentFileUri :
//         * exerciseAnalysis : 解析，省略190字
//         * isConfirnExpianation :
//         * rankNo : 1
//         * exerciseTypeCode : 30105
//         * exerciseOptionList : [{"optionId":"danxuan001","optionNo":"A","rankNo":1,"isRight":true,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"北京"},{"optionId":"danxuan002","optionNo":"B","rankNo":2,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"上海"},{"optionId":"danxuan003","optionNo":"C","rankNo":3,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"8c330e9bb90e4904aeca29aa8e5c6997","optionContent":"广州"}]
//         */
//
//        public String exercisesTitle;
//        public String exerciseExplainFileUri;
//        public String exerciseAnswer;
//        public String analysisFileUri;
//        public boolean isMistakesCollection;
//        public String studentOptionId;
//        public String workResultComment;
//        public String answerFileUri;
//        public String exercisesId;
//        public int score;
//        public String isRight;
//        public String studentAnswerFileUri;
//        public String workContentFileUri;
//        public String exerciseAnalysis;
//        public String isConfirnExpianation;
//        public int rankNo;
//        public int exerciseTypeCode;
//        public List<ExerciseOptionListBean> exerciseOptionList;
//
//        public static class ExerciseOptionListBean {
//            /**
//             * optionId : danxuan001
//             * optionNo : A
//             * rankNo : 1
//             * isRight : true
//             * createTime : 2017-06-03 15:59:59
//             * createUser : 001
//             * lastUpdateTime : 2017-06-03 15:59:59
//             * lastUpdateUser : 001
//             * isDeleted : false
//             * exerciseId : 8c330e9bb90e4904aeca29aa8e5c6997
//             * optionContent : 北京
//             */
//
//            public String optionId;
//            public String optionNo;
//            public int rankNo;
//            public boolean isRight;
//            public boolean ischeck;
//            public String createTime;
//            public String createUser;
//            public String lastUpdateTime;
//            public String lastUpdateUser;
//            public boolean isDeleted;
//            public String exerciseId;
//            public String optionContent;
//        }
//    }
}
