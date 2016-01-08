package com.edu.nc.bytesoft.dao.sql;

public class Queries {
    public static final String QUERY_GET_OBJECT_BY_ID = "SELECT ATYPE.CODE, " +
            "ATTS.VALUE, ATTS.DATE_VALUE, OBJR.REFERENCE\n" +
            "FROM ATTRTYPE ATYPE\n" +
            "LEFT JOIN ATTRIBUTES ATTS ON (ATTS.ATTR_ID = ATYPE.ATTR_ID AND ATTS.OBJECT_ID = ?)\n" +
            "LEFT JOIN OBJREFERENCE OBJR ON (OBJR.ATTR_ID = ATYPE.ATTR_ID AND OBJR.OBJECT_ID = ?)\n" +
            "WHERE ATYPE.OBJECT_TYPE_ID IN \n" +
            "  (\n" +
            "    SELECT OBT.OBJECT_TYPE_ID\n" +
            "    FROM OBJTYPE OBT\n" +
            "    START WITH OBT.OBJECT_TYPE_ID = \n" +
            "      (\n" +
            "        SELECT OBJECT_TYPE_ID\n" +
            "        FROM OBJECTS\n" +
            "        WHERE OBJECT_ID = ?\n" +
            "      )\n" +
            "    CONNECT BY PRIOR OBT.PARENT_ID = OBT.OBJECT_TYPE_ID\n" +
            "  )\n" +
            "AND (ATTS.VALUE IS NOT NULL \n" +
            "OR ATTS.DATE_VALUE IS NOT NULL\n" +
            "OR OBJR.REFERENCE IS NOT NULL)";

    public static final String QUERY_GET_ATTRIBUTES_BY_TYPE = "SELECT CODE\n" +
            "FROM ATTRTYPE\n" +
            "WHERE OBJECT_TYPE_ID IN \n" +
            "                    (\n" +
            "                      SELECT OBT.OBJECT_TYPE_ID\n" +
            "                      FROM OBJTYPE OBT\n" +
            "                      START WITH OBT.OBJECT_TYPE_ID = \n" +
            "                      \n" +
            "                        (\n" +
            "                          SELECT OBJECT_TYPE_ID\n" +
            "                          FROM OBJTYPE\n" +
            "                          WHERE CODE = ?\n" +
            "                          AND ROWNUM = 1\n" +
            "                        )\n" +
            "                      CONNECT BY PRIOR OBT.PARENT_ID = OBT.OBJECT_TYPE_ID\n" +
            "                    )";

    public static final String QUERY_INSERT_OBJECT = "INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, NAME) SELECT OBJ_SEQ.NEXTVAL, OBJT.OBJECT_TYPE_ID, ? FROM OBJTYPE OBJT WHERE OBJT.CODE = ? AND ROWNUM = 1";

    public static final String QUERY_GET_OBJ_SEQ_CURRVAL = "SELECT OBJ_SEQ.CURRVAL FROM DUAL";

    public static final String QUERY_GET_SINGLE_ATTRIBUTE = "SELECT * FROM tableName WHERE ATTR_ID = ? AND OBJECT_ID = ?";

    public static final String QUERY_GET_LIST_OBJECT_TYPE_CODE_BY_ATTR_ID = "SELECT REF_TYPE.CODE\n" +
            "FROM ATTRTYPE TYPE JOIN OBJTYPE REF_TYPE ON (REF_TYPE.OBJECT_TYPE_ID = TYPE.OBJECT_TYPE_ID_REF)\n" +
            "WHERE TYPE.ATTR_ID = ?";

    public static final String QUERY_GET_LIST_OBJECT_ID_BY_LIST_ITEM_OBJECT_TYPE_CODE = "SELECT OBJR.REFERENCE\n" +
            "FROM OBJREFERENCE OBJR, OBJECTS OBJ, OBJTYPE OBJ_T\n" +
            "WHERE OBJ.OBJECT_ID = OBJR.OBJECT_ID\n" +
            "AND OBJ.OBJECT_TYPE_ID = OBJ_T.OBJECT_TYPE_ID\n" +
            "AND OBJ_T.CODE = ?\n" +
            "AND ROWNUM = 1";

    public static final String QUERY_GET_ATTRIBUTE_ID_BY_CODE = "SELECT ATTR_ID\n" +
            " FROM ATTRTYPE\n" +
            " WHERE CODE = ?\n" +
            " AND ROWNUM = 1";

    public static final String QUERY_CHECK_IF_OBJECT_EXIST = "SELECT CASE\n" +
            "       WHEN EXISTS (SELECT 1\n" +
            "                    FROM OBJECTS\n" +
            "                    WHERE OBJECT_ID = ?)\n" +
            "       THEN 'Y'\n" +
            "       ELSE 'N'\n" +
            "       END REC_EXISTS\n" +
            "FROM DUAL";

    public static final String QUERY_GET_OBJECT_IDS_BY_TYPE_CODE = "SELECT OBJECT_ID\n" +
            "FROM OBJECTS OBJ\n" +
            "JOIN OBJTYPE OBJT ON (OBJ.OBJECT_TYPE_ID = OBJT.OBJECT_TYPE_ID)\n" +
            "WHERE OBJT.CODE = ?";
}
