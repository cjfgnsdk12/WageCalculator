package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import javafx.beans.property.SimpleStringProperty;
public class factory {
	public void saveConfig(ArrayList<cost> wageUnit) {
		try {
			//System.out.println(jarLocation());
			BufferedWriter out = new BufferedWriter(new FileWriter(jarLocation()+"\\config\\config.txt"));
			for(int i=0; i<wageUnit.size();i++) {
				String str=wageUnit.get(i).getName()+"="+wageUnit.get(i).getCost();
				out.write(str);
				out.newLine();
			}
			out.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void loadConfig(ArrayList<cost> wageUnit){
		try {
			BufferedReader br=new BufferedReader(new FileReader(jarLocation()+ "\\config\\config.txt"));
			String content=null;
			while(null!= (content=br.readLine())) {
				String arContent[]=content.split("=");
				cost cost= new cost(new SimpleStringProperty(arContent[0]),new SimpleStringProperty(arContent[1]));
				wageUnit.add(cost);
			}
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private String jarLocation() {
		String path=null;
		String decodedPath=null;
		
		try {
			path = factory.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			decodedPath = URLDecoder.decode(path, "UTF-8");
			decodedPath= new File(decodedPath).getParentFile().getPath();
			File file=new File(decodedPath+ "\\config\\");
			File file2=new File(decodedPath+ "\\complete\\");
			
			if(!file.exists()) {
				file.mkdirs();
			}
			if(!file2.exists()) {
				file2.mkdirs();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return decodedPath;
	}
	public String convert(HSSFWorkbook workbook,ArrayList<cost> wageUnit,String name) {
		String errorMsg="";
		HSSFSheet sheet=null;
		HSSFRow row=null;
		HSSFCell cell=null;
		
		try {
			DataFormatter formatter = new DataFormatter();
			//sheet 차례대로 불러오기
			for(int sheetIndex=0; sheetIndex<workbook.getNumberOfSheets();sheetIndex++) {
				sheet=workbook.getSheetAt(sheetIndex);
				System.out.println("------------------------------------------------------------");
				
				String[][] valueAr=new String[35][30];
				int sumCell=0;
				//핼 불러오기
				for(int rowIndex=0;rowIndex<sheet.getPhysicalNumberOfRows();rowIndex++) {
					row=sheet.getRow(rowIndex);
					
					// 행이 중간에 비어 오류가 있을시 메세지를 남기면서 그냥 넘어감  -오류가능성이 있어 메세지 남김-
					if(row==null) {
						errorMsg=errorMsg+sheet.getSheetName()+" "+(cell.getRowIndex()+1)+"행에서 오류가 있을수도 있습니다. 확인하세요\n";
						continue;
					}
					if(rowIndex==0) {
						sumCell=row.getPhysicalNumberOfCells();
					}
					
					//열 읽는 for문
					for(int cellIndex=0;cellIndex<row.getPhysicalNumberOfCells();cellIndex++) {
						cell=row.getCell(cellIndex);
						if(cell==null) {
							row.createCell(cellIndex).setCellValue("");
							cell=row.getCell(cellIndex);
							errorMsg=errorMsg+sheet.getSheetName()+" "+(cell.getRowIndex()+1)+"행 "+(cell.getColumnIndex()+1)+"열에서 오류가 있을수도 있습니다. 확인하세요\n";
						}
						String value = "";
						switch (cell.getCellTypeEnum()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
	                    case NUMERIC:
	                        //value = cell.getNumericCellValue() + "";
	                    	value=formatter.formatCellValue(cell);
	                    	value.trim();
	                        break;
	                    case STRING:
	                        //value = cell.getStringCellValue() + "";
	                    	value=formatter.formatCellValue(cell);
	                    	value.trim();
	                    	value = value.replaceAll(" " , "");
	                    	value = value.replaceAll("\\p{Z}", "");
	                        break;
	                    case BLANK:
	                        //value = cell.getBooleanCellValue() + "";
	                    	value="0";
	                        break;
	                    case ERROR:
	                        value = cell.getErrorCellValue() + "";
	                        System.out.println("에러타입");
	                        break;
						default:
							value=formatter.formatCellValue(cell);
							System.out.println("타입 이상해");
							break;
	                    }
						valueAr[rowIndex][cellIndex]=value;
	                    System.out.println(cellIndex+" : "+value);
					}//열 읽는 for문 끝
					
				}// 행 읽는 for문 끝
				int sumRow=0;
				
				//합계의 행을 구함
				for(int i=1; i<35;i++) {
					if(!isNum(valueAr[i][0])) {
						sumRow=i;
						break;
					}
				}
				System.out.println(sumRow+": 합계의행");
				/*for(int i=0;i<30;i++) {
					if(valueAr[0][i]==null) {
						sumCell=i;
					}
				}*/
				System.out.println(sumCell+": 합계의열");
				for(int c=1;c<sumCell;c++) {
					int sum=0;
					for(int r=1;r<sumRow;r++) {
						try {
						sum=sum+Integer.parseInt(valueAr[r][c]); 
						}catch(Exception e){
							continue;
						}
					}
					valueAr[sumRow][c]=sum+"";
					System.out.println(valueAr[sumRow][c]);
				}
				
				row=sheet.getRow(sumRow);
				
				//합계 세서 넣어주기
				for(int i=1;i<sumCell;i++) {
					try {
					cell=row.getCell(i);
					cell.setCellValue(valueAr[sumRow][i]);
					//System.out.println("값 입력");
					}catch(Exception e) {
						continue;
					}
				}
				
				// 합계와 단가를 곱함
				for(int i=1;i<sumCell;i++) {
					for(int j=0;j<wageUnit.size();j++) {
						if(valueAr[0][i].equals(wageUnit.get(j).getName())) {
							valueAr[sumRow+1][i]=(Integer.parseInt(valueAr[sumRow][i])*Integer.parseInt(wageUnit.get(j).getCost()))+"";
						}
					}
					if(valueAr[sumRow+1][i]==null) {
						valueAr[sumRow+1][i]="0";
					}
					System.out.println(valueAr[sumRow+1][i]);
				}
				// 총합 구하기
				int sumAll=0;
				for(int i=1;i<sumCell;i++) {
					sumAll=sumAll+  Integer.parseInt(valueAr[sumRow+1][i]); 
				}
				valueAr[sumRow+1][sumCell]=sumAll+"";
				
				// 합계 넣어주기
				for(int i=1;i<sumCell+1;i++) {
					row=sheet.createRow(sumRow+1+i);
					cell=row.createCell(0);
					if(i==sumCell) {
						cell.setCellValue("총합="+valueAr[sumRow+1][i]+"+ 나머지 더해야됨");	
					}else {
						cell.setCellValue(valueAr[0][i]+"*"+valueAr[sumRow][i]+"="+valueAr[sumRow+1][i]);	
					}
					
				}
					
				
			}//시트 세는 for문 끝
			
			
			// 엑셀 저장하는기능
			FileOutputStream fos=new FileOutputStream(jarLocation()+ "\\complete\\"+name);
			workbook.write(fos);
			fos.close();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(sheet.getSheetName()+" "+cell.getRowIndex()+"행에서 오류가 났습니다. 확인하세요");
		}
		return errorMsg;
	}
	/**
	 * 숫자인지 안니지 확인하는 메소드
	 * @param str
	 * @return
	 */
	private boolean isNum(String str) {
		boolean result = false;
        try{
            Integer.parseInt(str);
            result = true ;
        }catch(Exception e){}

        return result ;
	}
	
}
