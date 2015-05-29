/* Copyright (C) 2013  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.wikipathways.wp2rdf;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class StringMatrix {

        private Map<Integer,Map<Integer,String>> matrix;
        private int cols;
        private int rows;
        private Map<Integer,String> rowHeaders;
        private Map<Integer,String> colHeaders;

        public StringMatrix() {
                cols = 0;
                rows = 0;
        }

        private void checkDimensions(int row, int col) {
                checkRows(row);
                checkCols(col);
        }
        private void checkRows(int row) {
                if (row < 1 || row > rows)
                        throw new ArrayIndexOutOfBoundsException(
                                "Incorrect row number: " + row
                        );
        }
        private void checkCols(int col) {
                if (col < 1 || col > cols)
                        throw new ArrayIndexOutOfBoundsException(
                                "Incorrect column number: " + col
                        );
        }

        public String get(int row, int col) {
                checkDimensions(row, col);

                if (matrix == null) return "";
                Map<Integer,String> matrixRow = matrix.get(row);
                if (matrixRow == null) return "";

                return matrixRow.get(col);
        }

        public String get(int row, String col) {
                checkRows(row);
                return get(row, getColumnNumber(col));
        }

        public int getColumnCount() {
                return this.cols;
        }

        public boolean hasColumn(String col) {
            if (colHeaders == null) return false;

            return colHeaders.containsValue(col);
        }

        public int getColumnNumber(String col) {
                if (colHeaders != null) {
                        for (Integer colIndex : colHeaders.keySet()) {
                                String colname = colHeaders.get(colIndex);
                                if (colname != null && colname.equals(col))
                                        return colIndex;
                        }
                }
                throw new IllegalAccessError(
                        "No column found with this label."
                );
        }

        public String getColumnName(int index) {
                checkCols(index);

                if (colHeaders == null) return "";
                return colHeaders.get(index);
        }

        public int getRowCount() {
                return this.rows;
        }

        public String getRowName(int index) {
                checkRows(index);

                if (rowHeaders == null) return "";
                return rowHeaders.get(index);
        }

        public boolean hasColHeader() {
                return colHeaders != null;
        }

        public boolean hasRowHeader() {
                return rowHeaders != null;
        }

        public void set(int row, int col, String value) {
                if (row > rows) rows = row;
                if (col > cols) cols = col;
                checkDimensions(row, col);

                if (matrix == null)
                        matrix = new Hashtable<Integer, Map<Integer,String>>(row);
                Map<Integer,String> matrixRow = matrix.get(row);
                if (matrixRow == null) {
                        matrixRow = new Hashtable<Integer, String>(col);
                        matrix.put(row, matrixRow);
                }
                
                matrixRow.put(col, value);
        }

        public void setColumnName(int index, String name) {
                if (index > cols) cols = index;
                checkCols(index);

                if (colHeaders == null)
                        colHeaders = new Hashtable<Integer,String>();
                colHeaders.put(index, name);
        }

        public void setRowName(int index, String name) {
                checkRows(index);

                if (rowHeaders == null)
                        rowHeaders = new Hashtable<Integer,String>();
                rowHeaders.put(index, name);
        }

        public void setSize(int row, int col) {
                this.rows = row;
                this.cols = col;
        }

        public void set(int row, String col, String value) {
                checkRows(row);
                set(row, getColumnNumber(col), value);
        }

        public List<String> getColumn(int col) {
                checkCols(col);

                int rowCount = getRowCount();
                List<String> results = new ArrayList<String>(rowCount);
                for (int i=1; i<=rowCount; i++) {
                        String result = get(i,col);
                        results.add(result == null ? "" : result);
                }
                return results;
        }

        public List<String> getColumn(String col) {
                int colNo = getColumnNumber(col);
                return getColumn(colNo);
        }

        public String toString() {
                StringBuffer buffer = new StringBuffer();
                buffer.append('[');
                if (hasColHeader()) {
                        buffer.append("[");
                        for (int col=1; col<=getColumnCount(); col++) {
                                buffer.append('"');
                                String result = getColumnName(col);
                                buffer.append(result == null ? "" : result);
                                buffer.append('"');
                                if (col<getColumnCount()) buffer.append(',');
                        }
                        buffer.append("],\n");
                }
                for (int row=1; row<=getRowCount(); row++) {
                        if (hasRowHeader()) {
                                buffer.append('"');
                                String result = getRowName(row);
                                buffer.append(result == null ? "" : result);
                                buffer.append("\": ");
                        }
                        buffer.append('[');
                        for (int col=1; col<=getColumnCount(); col++) {
                                buffer.append('"');
                                String result = get(row,col);
                                buffer.append(result == null ? "" : result);
                                buffer.append('"');
                                if (col<getColumnCount()) buffer.append(',');
                        }
                        buffer.append(']');
                        if (row<getRowCount()) buffer.append(',');
                        buffer.append('\n');
                }
                buffer.append(']');
                buffer.append('\n');
                return buffer.toString();
        }

        public List<String> getColumnNames() {
                List<String> names = new ArrayList<String>(getColumnCount());
                for (int i=1; i<=getColumnCount(); i++)
                        names.add(getColumnName(i));
                return names;
        }

        public List<String> getRowNames() {
                List<String> names = new ArrayList<String>(getRowCount());
                for (int i=1; i<=getRowCount(); i++)
                        names.add(getRowName(i));
                return names;
        }
        
}
