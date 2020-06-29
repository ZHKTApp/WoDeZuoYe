package com.zwyl.myhomework.main.subject;

import com.zwyl.myhomework.base.ComFlag;

import java.util.List;

public class BeanSubjectFile {


    /**
     * isSeeAnswer : true
     * fileWorkAnswerCardMapList : [{"exerciseId":"cardexerciseid002","exerciseScore":5,"exerciseAnswerFileUri":"","sumScore":10,"exerciseAnswer":null,"isMistakesCollection":false,"exerciseOptionList":[{"optionId":"optionid003","optionNo":"A"},{"optionId":"optionid004","optionNo":"B"}],"cardExerciseNo":2,"cardTitle":"选出下面正确的选项","exerciseNo":2,"workResultComment":"","isRight":"","exerciseAnalysisFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/451646","cardTypeCode":30101,"exerciseAnalysis":"","studentAnswerOptionId":"","isConfirnExpianation":"","rankNo":1},{"exerciseId":"cardexerciseid002","exerciseScore":5,"exerciseAnswerFileUri":"","sumScore":10,"exerciseAnswer":null,"isMistakesCollection":false,"exerciseOptionList":[{"optionId":"optionid003","optionNo":"A"},{"optionId":"optionid004","optionNo":"B"}],"cardExerciseNo":2,"cardTitle":"选出下面正确的选项","exerciseNo":2,"workResultComment":"","isRight":"","exerciseAnalysisFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/451646","cardTypeCode":30101,"exerciseAnalysis":"","studentAnswerOptionId":"","isConfirnExpianation":"","rankNo":1},{"exerciseId":"cardexerciseid003","exerciseScore":60,"exerciseAnswerFileUri":"","sumScore":60,"exerciseAnswer":"","isMistakesCollection":false,"cardExerciseNo":1,"exerciseOptionList":"","cardTitle":"计算下列结果","exerciseNo":1,"workResultComment":"","studentAnswerFileUri":"","exerciseAnalysisFileUri":"http://art.test.internet.zhiwangyilian.com/images/artImg/451644545","workContentFileUri":"","cardTypeCode":30105,"exerciseAnalysis":"","isConfirnExpianation":"","rankNo":2}]
     * fileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/543556
     */

    public boolean isSeeAnswer;
    public String fileUri;
    public List<FileWorkAnswerCardMapListBean> fileWorkAnswerCardMapList;
    public String exerciseTotalScore;
    public String studentTotalScore;
    public boolean isSubmit;//是否提交
    public String workCommand;

    public static class FileWorkAnswerCardMapListBean {
        /**
         * exerciseId : cardexerciseid002
         * exerciseScore : 5
         * exerciseAnswerFileUri :
         * sumScore : 10
         * exerciseAnswer : null
         * isMistakesCollection : false
         * exerciseOptionList : [{"optionId":"optionid003","optionNo":"A"},{"optionId":"optionid004","optionNo":"B"}]
         * cardExerciseNo : 2
         * cardTitle : 选出下面正确的选项
         * exerciseNo : 2
         * workResultComment :
         * isRight :
         * exerciseAnalysisFileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/451646
         * cardTypeCode : 30101
         * exerciseAnalysis :
         * studentAnswerOptionId :
         * isConfirnExpianation :
         * rankNo : 1
         * studentAnswerFileUri :
         * workContentFileUri :
         */

        public String exerciseId;
        public String exerciseExplainFileUri;
        //修改
        public int exerciseScore;
        public String exerciseAnswerFileUri;
        public int sumScore;
        public String exerciseAnswer;
        public boolean isMistakesCollection;
        public String cardTitle;
        public int exerciseNo;
        public String workResultComment;
        public String isRight;
        public String exerciseAnalysisFileUri;
        public int cardTypeCode;
        public String exerciseAnalysis;
        public String studentAnswerOptionId;
        public boolean isConfirnExpianation; //true的话是不能点   false是能点
        public int rankNo;
        public String studentAnswerFileUri;
        public String workContentFileUri;
        public List<ExerciseOptionListBean> exerciseOptionList;

        public static class ExerciseOptionListBean {
            /**
             * optionId : optionid003
             * optionNo : A
             */

            public String optionId;
            public String optionNo;
            public boolean ischeck;

            @Override
            public String toString() {
                return "ExerciseOptionListBean{" +
                        "optionId='" + optionId + '\'' +
                        ", optionNo='" + optionNo + '\'' +
                        ", ischeck=" + ischeck +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "FileWorkAnswerCardMapListBean{" +
                    "exerciseId='" + exerciseId + '\'' +
                    ", exerciseExplainFileUri='" + exerciseExplainFileUri + '\'' +
                    ", exerciseScore=" + exerciseScore +
                    ", exerciseAnswerFileUri='" + exerciseAnswerFileUri + '\'' +
                    ", sumScore=" + sumScore +
                    ", exerciseAnswer='" + exerciseAnswer + '\'' +
                    ", isMistakesCollection=" + isMistakesCollection +
                    ", cardTitle='" + cardTitle + '\'' +
                    ", exerciseNo=" + exerciseNo +
                    ", workResultComment='" + workResultComment + '\'' +
                    ", isRight='" + isRight + '\'' +
                    ", exerciseAnalysisFileUri='" + exerciseAnalysisFileUri + '\'' +
                    ", cardTypeCode=" + cardTypeCode +
                    ", exerciseAnalysis='" + exerciseAnalysis + '\'' +
                    ", studentAnswerOptionId='" + studentAnswerOptionId + '\'' +
                    ", isConfirnExpianation='" + isConfirnExpianation + '\'' +
                    ", rankNo=" + rankNo +
                    ", studentAnswerFileUri='" + studentAnswerFileUri + '\'' +
                    ", workContentFileUri='" + workContentFileUri + '\'' +
                    ", exerciseOptionList=" + exerciseOptionList +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BeanSubjectFile{" +
                "isSeeAnswer=" + isSeeAnswer +
                ", fileUri='" + fileUri + '\'' +
                ", fileWorkAnswerCardMapList=" + fileWorkAnswerCardMapList +
                '}';
    }
}
