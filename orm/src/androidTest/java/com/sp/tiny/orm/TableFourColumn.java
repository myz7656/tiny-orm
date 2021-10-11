package com.sp.tiny.orm;

import com.sp.tiny.orm.annotation.Entity;
import com.sp.tiny.orm.annotation.Id;
import com.sp.tiny.orm.annotation.NotNull;
import com.sp.tiny.orm.annotation.Property;

import java.util.UUID;

@Entity(name = "table_four_column")
public class TableFourColumn {

    @Property(name = "_id")
    @Id
    private String mId = UUID.randomUUID().toString();

    @Property(name = "column_1")
    private int mColumn1 = 111;

    @Property(name = "column_2")
    @NotNull
    private double mColumn2 = 222.222;

    @Property(name = "column_3")
    private String mColumn3 = "column_3";

    @Property(name = "column_4")
    private String mColumn4 = "column_4";

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public int getColumn1() {
        return mColumn1;
    }

    public void setColumn1(int column1) {
        mColumn1 = column1;
    }

    public double getColumn2() {
        return mColumn2;
    }

    public void setColumn2(double column2) {
        mColumn2 = column2;
    }

    public String getColumn3() {
        return mColumn3;
    }

    public void setColumn3(String column3) {
        mColumn3 = column3;
    }

    public String getColumn4() {
        return mColumn4;
    }

    public void setColumn4(String column4) {
        mColumn4 = column4;
    }

    @Override
    public String toString() {
        return "TableFourColumn{" +
                "mId='" + mId + '\'' +
                ", mColumn1=" + mColumn1 +
                ", mColumn2=" + mColumn2 +
                ", mColumn3='" + mColumn3 + '\'' +
                ", mColumn4='" + mColumn4 + '\'' +
                '}';
    }
}
