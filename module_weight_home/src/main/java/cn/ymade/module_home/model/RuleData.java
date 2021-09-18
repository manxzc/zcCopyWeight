package cn.ymade.module_home.model;

import java.io.Serializable;
import java.util.List;

public class RuleData {

    /**
     * code : 1
     * Rule : [{"RuleID":"210590f9-98f2-449b-9a68-da5019e0bac9","Category":"货品","Matching":1,"StrKey":"01","StrStart":9,"StrLength":5,"Factor":0,"Status":1},{"RuleID":"fd151a5e-2a04-487c-9eaa-37920900300e","Category":"货品","Matching":1,"StrKey":"21","StrStart":13,"StrLength":5,"Factor":0,"Status":1},{"RuleID":"25990982-8231-4476-9a05-43b5aaab8448","Category":"量度","Matching":2,"StrKey":"3102","StrStart":0,"StrLength":6,"Factor":2,"Status":1},{"RuleID":"b75b57ea-79f8-4ce4-a8ae-62122b7b0a5f","Category":"量度","Matching":2,"StrKey":"3100","StrStart":0,"StrLength":6,"Factor":0,"Status":0},{"RuleID":"f0a49acf-3058-4eb2-a511-eec25a9c58da","Category":"量度","Matching":1,"StrKey":"","StrStart":0,"StrLength":6,"Factor":2,"Status":0},{"RuleID":"f68aed8b-3345-4f2f-8787-349a7e09b68a","Category":"日期","Matching":3,"StrKey":"11","StrStart":0,"StrLength":0,"Factor":0,"Status":0},{"RuleID":"f68a4ca2-3093-4c2a-a179-08f1b50e2811","Category":"日期","Matching":3,"StrKey":"13","StrStart":0,"StrLength":6,"Factor":0,"Status":0},{"RuleID":"de66c76b-16ed-4328-900f-08d6674c5dfa","Category":"日期","Matching":3,"StrKey":"17","StrStart":0,"StrLength":6,"Factor":0,"Status":0},{"RuleID":"ae2eb5cb-37a4-4edd-8ee2-4a06fc08a145","Category":"批号","Matching":1,"StrKey":"21","StrStart":0,"StrLength":0,"Factor":0,"Status":0}]
     */

    private String code;
    private List<RuleBean> Rule;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RuleBean> getRule() {
        return Rule;
    }

    public void setRule(List<RuleBean> Rule) {
        this.Rule = Rule;
    }

    public static class RuleBean implements Serializable {
        /**
         * RuleID : 210590f9-98f2-449b-9a68-da5019e0bac9
         * Category : 货品
         * Matching : 1
         * StrKey : 01
         * StrStart : 9
         * StrLength : 5
         * Factor : 0
         * Status : 1
         */

        private String RuleID;
        private String Category;
        private Integer Matching;
        private String StrKey;
        private Integer StrStart;
        private Integer StrLength;
        private Integer Factor;
        private Integer Status;

        public String getRuleID() {
            return RuleID;
        }

        public void setRuleID(String RuleID) {
            this.RuleID = RuleID;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public Integer getMatching() {
            return Matching;
        }

        public void setMatching(Integer Matching) {
            this.Matching = Matching;
        }

        public String getStrKey() {
            return StrKey;
        }

        public void setStrKey(String StrKey) {
            this.StrKey = StrKey;
        }

        public Integer getStrStart() {
            return StrStart;
        }

        public void setStrStart(Integer StrStart) {
            this.StrStart = StrStart;
        }

        public Integer getStrLength() {
            return StrLength;
        }

        public void setStrLength(Integer StrLength) {
            this.StrLength = StrLength;
        }

        public Integer getFactor() {
            return Factor;
        }

        public void setFactor(Integer Factor) {
            this.Factor = Factor;
        }

        public Integer getStatus() {
            return Status;
        }

        public void setStatus(Integer Status) {
            this.Status = Status;
        }
    }
}
