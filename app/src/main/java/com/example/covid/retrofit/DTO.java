package com.example.covid.retrofit;

import org.simpleframework.xml.*;

import java.util.List;

@Root(name="response", strict = false)
public class DTO {
    @Element(name="header")
    public Header header;

    @Element(name="body")
    public Body body;

    @Root(name="header", strict = false)
    public static class Header {
        @Element(name = "resultCode")
        public String resultCode;

        @Element(name = "resultMsg")
        public String resultMsg;
    }

    @Root(name="body", strict = false)
    public static class Body {
        @ElementList(entry = "items")
        public List<Item> items;

        @Element(name="numOfRows")
        public String numOfRows;

        @Element(name="pageNo")
        public String pageNo;

        @Element(name="totalCount")
        public String totalCount;
    }

    @Root(name="item", strict = false)
    public static class Item {
        @Element(name = "accDefRate", required = false)
        public String accDefRate;

        @Element(name = "accExamCnt", required = false)
        public int accExamCnt;

        @Element(name = "accExamCompCnt", required = false)
        public int accExamCompCnt;

        @Element(name = "careCnt", required = false)
        public String careCnt;

        @Element(name = "clearCnt", required = false)
        public int clearCnt;

        @Element(name = "createDt", required = false)
        public String createDt;

        @Element(name = "deathCnt", required = false)
        public int deathCnt;

        @Element(name = "decideCnt", required = false)
        public int decideCnt;

        @Element(name = "examCnt", required = false)
        public int examCnt;

        @Element(name = "resutlNegCnt", required = false)
        public int resutlNegCnt;

        @Element(name = "seq", required = false)
        public String seq;

        @Element(name = "stateDt", required = false)
        public String stateDt;

        @Element(name = "stateTime", required = false)
        public String stateTime;

        @Element(name = "updateDt", required = false)
        public String updateDt;
    }
}