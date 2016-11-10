package org.talend.components.jobs.filedelimited;

import static org.talend.components.jobs.filedelimited.FileInputDelimitedSettings.FILE_NAME;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.talend.components.stopwatch.StopWatch;

import routines.system.TDieException;

public class FileInputDelimitedProcess62 {

    public void tFileInputDelimited_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
        StopWatch watch = StopWatch.getInstance(10);
        // TODO Prepare process start ---------------------------------------------------------------------------------------------------
        watch.startStageHere(1);
        globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 0);

        String iterateId = "";

        String currentComponent = "";
        java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

        try {

            String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
            if (true) {
                
                row1Struct row1 = new row1Struct();
        
                // Prepare process end --------------------------------------------------------------------------------------------------
                watch.finishStageHere(1);
                
                // TODO Other component initialization start
                watch.startStageHere(2);
                
                /**
                 * [tJavaRow_1 begin ] start
                 */

                currentComponent = "tJavaRow_1";

                int tos_count_tJavaRow_1 = 0;

                int nb_line_tJavaRow_1 = 0;

                /**
                 * [tJavaRow_1 begin ] stop
                 */
                
                watch.finishStageHere(2);
                // Other component initialization end -----------------------------------------------------------------------------------

                // TODO Connect to source start -----------------------------------------------------------------------------------------
                watch.startStageHere(3);
              
                /**
                 * [tFileInputDelimited_2 begin ] start
                 */

                currentComponent = "tFileInputDelimited_2";

                int tos_count_tFileInputDelimited_2 = 0;

                final routines.system.RowState rowstate_tFileInputDelimited_2 = new routines.system.RowState();

                int nb_line_tFileInputDelimited_2 = 0;
                org.talend.fileprocess.FileInputDelimited fid_tFileInputDelimited_2 = null;
                try {

                    Object filename_tFileInputDelimited_2 = FILE_NAME;
                    if (filename_tFileInputDelimited_2 instanceof java.io.InputStream) {

                        int footer_value_tFileInputDelimited_2 = 0, random_value_tFileInputDelimited_2 = -1;
                        if (footer_value_tFileInputDelimited_2 > 0 || random_value_tFileInputDelimited_2 > 0) {
                            throw new java.lang.Exception(
                                    "When the input source is a stream,footer and random shouldn't be bigger than 0.");
                        }

                    }
                    try {
                        fid_tFileInputDelimited_2 = new org.talend.fileprocess.FileInputDelimited(
                                FILE_NAME, "US-ASCII", ";", "\n", false, 0, 0, -1, -1, false);
                    } catch (java.lang.Exception e) {

                        System.err.println(e.getMessage());

                    }

                    // Connect to source end ------------------------------------------------------------------------------------------------
                    watch.finishStageHere(3);
                    
                    // TODO Read record start -----------------------------------------------------------------------------------------------
                    // also see in the end of while loop
                    watch.startStageHere(4);
                    boolean continueOrNot = fid_tFileInputDelimited_2 != null && fid_tFileInputDelimited_2.nextRecord();
                    // Read record end ------------------------------------------------------------------------------------------------------
                    watch.finishStageHere(4);
                    while (continueOrNot) {
                        
                        // TODO Set record values to POJO row2struct start ------------------------------------------------------------------------
                        watch.startStageHere(5);
                        
                        rowstate_tFileInputDelimited_2.reset();

                        row1 = null;

                        boolean whetherReject_tFileInputDelimited_2 = false;
                        row1 = new row1Struct();
                        try {

                            int columnIndexWithD_tFileInputDelimited_2 = 0;

                            columnIndexWithD_tFileInputDelimited_2 = 0;

                            row1.Column1 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 1;

                            row1.Column2 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 2;

                            row1.Column3 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 3;

                            row1.Column4 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 4;

                            row1.Column5 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 5;

                            row1.Column6 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 6;

                            row1.Column7 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 7;

                            row1.Column8 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 8;

                            row1.Column9 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 9;

                            row1.Column10 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 10;

                            row1.Column11 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 11;

                            row1.Column12 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 12;

                            row1.Column13 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 13;

                            row1.Column14 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 14;

                            row1.Column15 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 15;

                            row1.Column16 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 16;

                            row1.Column17 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 17;

                            row1.Column18 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 18;

                            row1.Column19 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 19;

                            row1.Column20 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 20;

                            row1.Column21 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 21;

                            row1.Column22 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 22;

                            row1.Column23 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 23;

                            row1.Column24 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 24;

                            row1.Column25 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 25;

                            row1.Column26 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 26;

                            row1.Column27 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 27;

                            row1.Column28 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 28;

                            row1.Column29 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            columnIndexWithD_tFileInputDelimited_2 = 29;

                            row1.Column30 = fid_tFileInputDelimited_2.get(columnIndexWithD_tFileInputDelimited_2);

                            if (rowstate_tFileInputDelimited_2.getException() != null) {
                                throw rowstate_tFileInputDelimited_2.getException();
                            }

                        } catch (java.lang.Exception e) {
                            whetherReject_tFileInputDelimited_2 = true;

                            System.err.println(e.getMessage());
                            row1 = null;

                        }

                        /**
                         * [tFileInputDelimited_2 begin ] stop
                         */

                        /**
                         * [tFileInputDelimited_2 main ] start
                         */

                        currentComponent = "tFileInputDelimited_2";

                        tos_count_tFileInputDelimited_2++;
                        
                        // Set record values to POJO row2struct end  ------------------------------------------------------------------------
                        watch.finishStageHere(5);
                        
                        /**
                         * [tFileInputDelimited_2 main ] stop
                         */
                        
                        // TODO other component main part start -----------------------------------------------------------------------------
                        watch.startStageHere(6);
                        
                        // Start of branch "row1"
                        if (row1 != null) {

                            /**
                             * [tJavaRow_1 main ] start
                             */

                            currentComponent = "tJavaRow_1";

                            // code sample:
                            //
                            // multiply by 2 the row identifier
                            // output_row.id = row1.id * 2;
                            //
                            // lowercase the name
                            // output_row.name = row1.name.toLowerCase();

                            nb_line_tJavaRow_1++;

                            tos_count_tJavaRow_1++;

                            /**
                             * [tJavaRow_1 main ] stop
                             */

                        } // End of branch "row1"

                        /**
                         * [tFileInputDelimited_2 end ] start
                         */

                        currentComponent = "tFileInputDelimited_2";
                        
                        // other component main part end -----------------------------------------------------------------------------
                        watch.finishStageHere(6);

                        // TODO Read record start -----------------------------------------------------------------------------------------------
                        // also see in the before while loop
                        watch.startStageHere(4);
                        continueOrNot = fid_tFileInputDelimited_2 != null && fid_tFileInputDelimited_2.nextRecord();
                        // Read record end ------------------------------------------------------------------------------------------------------
                        watch.finishStageHere(4);
                    }
                    // TODO Close connection start ----------------------------------------------------------------------------------------------
                    watch.startStageHere(7);
                } finally {
                    if (!((Object) (FILE_NAME) instanceof java.io.InputStream)) {
                        if (fid_tFileInputDelimited_2 != null) {
                            fid_tFileInputDelimited_2.close();
                        }
                    }
                    if (fid_tFileInputDelimited_2 != null) {
                        globalMap.put("tFileInputDelimited_2_NB_LINE", fid_tFileInputDelimited_2.getRowNumber());

                    }
                }

                // Close connection end ----------------------------------------------------------------------------------------------
                watch.finishStageHere(7);
                
                /**
                 * [tFileInputDelimited_2 end ] stop
                 */

                // TODO Other component closing start ----------------------------------------------------------------------------------
                watch.startStageHere(8);
                
                /**
                 * [tJavaRow_1 end ] start
                 */

                currentComponent = "tJavaRow_1";

                globalMap.put("tJavaRow_1_NB_LINE", nb_line_tJavaRow_1);

                /**
                 * [tJavaRow_1 end ] stop
                 */
                // Other component closing end ----------------------------------------------------------------------------------
                watch.finishStageHere(8);

            } // end the resume

            // TODO finalization start -----------------------------------------------------------------------------------------------
            watch.startStageHere(9);
            
        } catch (java.lang.Exception e) {

            TalendException te = new TalendException(e, currentComponent, globalMap);

            throw te;
        } catch (java.lang.Error error) {

            throw error;
        } finally {

            try {

                /**
                 * [tFileInputDelimited_2 finally ] start
                 */

                currentComponent = "tFileInputDelimited_2";

                /**
                 * [tFileInputDelimited_2 finally ] stop
                 */

                /**
                 * [tJavaRow_1 finally ] start
                 */

                currentComponent = "tJavaRow_1";

                /**
                 * [tJavaRow_1 finally ] stop
                 */

            } catch (java.lang.Exception e) {
                // ignore
            } catch (java.lang.Error error) {
                // ignore
            }
            resourceMap = null;
        }

        globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 1);
        
        // TODO finalization end -----------------------------------------------------------------------------------------------
        watch.finishStageHere(9);
    }

    private class TalendException extends Exception {

        private static final long serialVersionUID = 1L;

        private java.util.Map<String, Object> globalMap = null;

        private Exception e = null;

        private String currentComponent = null;

        private String virtualComponentName = null;

        public void setVirtualComponentName(String virtualComponentName) {
            this.virtualComponentName = virtualComponentName;
        }

        private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
            this.currentComponent = errorComponent;
            this.globalMap = globalMap;
            this.e = e;
        }

        public Exception getException() {
            return this.e;
        }

        public String getCurrentComponent() {
            return this.currentComponent;
        }

        public String getExceptionCauseMessage(Exception e) {
            Throwable cause = e;
            String message = null;
            int i = 10;
            while (null != cause && 0 < i--) {
                message = cause.getMessage();
                if (null == message) {
                    cause = cause.getCause();
                } else {
                    break;
                }
            }
            if (null == message) {
                message = e.getClass().getName();
            }
            return message;
        }

        @Override
        public void printStackTrace() {
            if (!(e instanceof TalendException || e instanceof TDieException)) {
                if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
                    globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
                }
                globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
                System.err.println("Exception in component " + currentComponent);
            }
            if (!(e instanceof TDieException)) {
                if (e instanceof TalendException) {
                    e.printStackTrace();
                } else {
                    e.printStackTrace();
                }
            }
            if (!(e instanceof TalendException)) {
                try {
                    for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
                        if (m.getName().compareTo(currentComponent + "_error") == 0) {
                            break;
                        }
                    }

                    if (!(e instanceof TDieException)) {
                    }
                } catch (Exception e) {
                    this.e.printStackTrace();
                }
            }
        }
    }

    public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {

        final static byte[] commonByteArrayLock_LOCAL_PROJECT_fileInputDelimited = new byte[0];

        static byte[] commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[0];

        public String Column1;

        public String getColumn1() {
            return this.Column1;
        }

        public String Column2;

        public String getColumn2() {
            return this.Column2;
        }

        public String Column3;

        public String getColumn3() {
            return this.Column3;
        }

        public String Column4;

        public String getColumn4() {
            return this.Column4;
        }

        public String Column5;

        public String getColumn5() {
            return this.Column5;
        }

        public String Column6;

        public String getColumn6() {
            return this.Column6;
        }

        public String Column7;

        public String getColumn7() {
            return this.Column7;
        }

        public String Column8;

        public String getColumn8() {
            return this.Column8;
        }

        public String Column9;

        public String getColumn9() {
            return this.Column9;
        }

        public String Column10;

        public String getColumn10() {
            return this.Column10;
        }

        public String Column11;

        public String getColumn11() {
            return this.Column11;
        }

        public String Column12;

        public String getColumn12() {
            return this.Column12;
        }

        public String Column13;

        public String getColumn13() {
            return this.Column13;
        }

        public String Column14;

        public String getColumn14() {
            return this.Column14;
        }

        public String Column15;

        public String getColumn15() {
            return this.Column15;
        }

        public String Column16;

        public String getColumn16() {
            return this.Column16;
        }

        public String Column17;

        public String getColumn17() {
            return this.Column17;
        }

        public String Column18;

        public String getColumn18() {
            return this.Column18;
        }

        public String Column19;

        public String getColumn19() {
            return this.Column19;
        }

        public String Column20;

        public String getColumn20() {
            return this.Column20;
        }

        public String Column21;

        public String getColumn21() {
            return this.Column21;
        }

        public String Column22;

        public String getColumn22() {
            return this.Column22;
        }

        public String Column23;

        public String getColumn23() {
            return this.Column23;
        }

        public String Column24;

        public String getColumn24() {
            return this.Column24;
        }

        public String Column25;

        public String getColumn25() {
            return this.Column25;
        }

        public String Column26;

        public String getColumn26() {
            return this.Column26;
        }

        public String Column27;

        public String getColumn27() {
            return this.Column27;
        }

        public String Column28;

        public String getColumn28() {
            return this.Column28;
        }

        public String Column29;

        public String getColumn29() {
            return this.Column29;
        }

        public String Column30;

        public String getColumn30() {
            return this.Column30;
        }

        private String readString(ObjectInputStream dis) throws IOException {
            String strReturn = null;
            int length = 0;
            length = dis.readInt();
            if (length == -1) {
                strReturn = null;
            } else {
                if (length > commonByteArray_LOCAL_PROJECT_fileInputDelimited.length) {
                    if (length < 1024 && commonByteArray_LOCAL_PROJECT_fileInputDelimited.length == 0) {
                        commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[1024];
                    } else {
                        commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[2 * length];
                    }
                }
                dis.readFully(commonByteArray_LOCAL_PROJECT_fileInputDelimited, 0, length);
                strReturn = new String(commonByteArray_LOCAL_PROJECT_fileInputDelimited, 0, length, "UTF8");
            }
            return strReturn;
        }

        private void writeString(String str, ObjectOutputStream dos) throws IOException {
            if (str == null) {
                dos.writeInt(-1);
            } else {
                byte[] byteArray = str.getBytes("UTF8");
                dos.writeInt(byteArray.length);
                dos.write(byteArray);
            }
        }

        public void readData(ObjectInputStream dis) {

            synchronized (commonByteArrayLock_LOCAL_PROJECT_fileInputDelimited) {

                try {

                    int length = 0;

                    this.Column1 = readString(dis);

                    this.Column2 = readString(dis);

                    this.Column3 = readString(dis);

                    this.Column4 = readString(dis);

                    this.Column5 = readString(dis);

                    this.Column6 = readString(dis);

                    this.Column7 = readString(dis);

                    this.Column8 = readString(dis);

                    this.Column9 = readString(dis);

                    this.Column10 = readString(dis);

                    this.Column11 = readString(dis);

                    this.Column12 = readString(dis);

                    this.Column13 = readString(dis);

                    this.Column14 = readString(dis);

                    this.Column15 = readString(dis);

                    this.Column16 = readString(dis);

                    this.Column17 = readString(dis);

                    this.Column18 = readString(dis);

                    this.Column19 = readString(dis);

                    this.Column20 = readString(dis);

                    this.Column21 = readString(dis);

                    this.Column22 = readString(dis);

                    this.Column23 = readString(dis);

                    this.Column24 = readString(dis);

                    this.Column25 = readString(dis);

                    this.Column26 = readString(dis);

                    this.Column27 = readString(dis);

                    this.Column28 = readString(dis);

                    this.Column29 = readString(dis);

                    this.Column30 = readString(dis);

                } catch (IOException e) {
                    throw new RuntimeException(e);

                }

            }

        }

        public void writeData(ObjectOutputStream dos) {
            try {

                // String

                writeString(this.Column1, dos);

                // String

                writeString(this.Column2, dos);

                // String

                writeString(this.Column3, dos);

                // String

                writeString(this.Column4, dos);

                // String

                writeString(this.Column5, dos);

                // String

                writeString(this.Column6, dos);

                // String

                writeString(this.Column7, dos);

                // String

                writeString(this.Column8, dos);

                // String

                writeString(this.Column9, dos);

                // String

                writeString(this.Column10, dos);

                // String

                writeString(this.Column11, dos);

                // String

                writeString(this.Column12, dos);

                // String

                writeString(this.Column13, dos);

                // String

                writeString(this.Column14, dos);

                // String

                writeString(this.Column15, dos);

                // String

                writeString(this.Column16, dos);

                // String

                writeString(this.Column17, dos);

                // String

                writeString(this.Column18, dos);

                // String

                writeString(this.Column19, dos);

                // String

                writeString(this.Column20, dos);

                // String

                writeString(this.Column21, dos);

                // String

                writeString(this.Column22, dos);

                // String

                writeString(this.Column23, dos);

                // String

                writeString(this.Column24, dos);

                // String

                writeString(this.Column25, dos);

                // String

                writeString(this.Column26, dos);

                // String

                writeString(this.Column27, dos);

                // String

                writeString(this.Column28, dos);

                // String

                writeString(this.Column29, dos);

                // String

                writeString(this.Column30, dos);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append("Column1=" + Column1);
            sb.append(",Column2=" + Column2);
            sb.append(",Column3=" + Column3);
            sb.append(",Column4=" + Column4);
            sb.append(",Column5=" + Column5);
            sb.append(",Column6=" + Column6);
            sb.append(",Column7=" + Column7);
            sb.append(",Column8=" + Column8);
            sb.append(",Column9=" + Column9);
            sb.append(",Column10=" + Column10);
            sb.append(",Column11=" + Column11);
            sb.append(",Column12=" + Column12);
            sb.append(",Column13=" + Column13);
            sb.append(",Column14=" + Column14);
            sb.append(",Column15=" + Column15);
            sb.append(",Column16=" + Column16);
            sb.append(",Column17=" + Column17);
            sb.append(",Column18=" + Column18);
            sb.append(",Column19=" + Column19);
            sb.append(",Column20=" + Column20);
            sb.append(",Column21=" + Column21);
            sb.append(",Column22=" + Column22);
            sb.append(",Column23=" + Column23);
            sb.append(",Column24=" + Column24);
            sb.append(",Column25=" + Column25);
            sb.append(",Column26=" + Column26);
            sb.append(",Column27=" + Column27);
            sb.append(",Column28=" + Column28);
            sb.append(",Column29=" + Column29);
            sb.append(",Column30=" + Column30);
            sb.append("]");

            return sb.toString();
        }

        /**
         * Compare keys
         */
        public int compareTo(row1Struct other) {

            int returnValue = -1;

            return returnValue;
        }

        private int checkNullsAndCompare(Object object1, Object object2) {
            int returnValue = 0;
            if (object1 instanceof Comparable && object2 instanceof Comparable) {
                returnValue = ((Comparable) object1).compareTo(object2);
            } else if (object1 != null && object2 != null) {
                returnValue = compareStrings(object1.toString(), object2.toString());
            } else if (object1 == null && object2 != null) {
                returnValue = 1;
            } else if (object1 != null && object2 == null) {
                returnValue = -1;
            } else {
                returnValue = 0;
            }

            return returnValue;
        }

        private int compareStrings(String string1, String string2) {
            return string1.compareTo(string2);
        }

    }
}
