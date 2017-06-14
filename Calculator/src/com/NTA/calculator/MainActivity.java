package com.NTA.calculator;

import java.util.Arrays;
import java.util.Stack;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

class Calculator{
	boolean check_error = false;  // Kiểm tra lỗi
	
	public String chuanHoaSo(double number){  
		int a = (int)number;
		if (a == number) 
			return Integer.toString(a);
		else return Double.toString(number);
	}
	
	public boolean checkCharPi(char c){ // Kiểm tra kí tự c có là pi hay không
		if (c == 'π') return true;
		else return false;
	}
	
	public boolean checkCharNumber(char c){	// Kiểm tra kí tự c có là số hay không? kí tự pi cũng là số
		if (Character.isDigit(c) || checkCharPi(c)) return true; // Character.isDigit(c): c là số -> true, ngược lại false 
		else return false;
	}
	
	public String NumToString(double number){ // Chuyển số sang chuỗi
		return chuanHoaSo(number);
	}
	
	public double StringToNum(String s){ 	// Chuyển chuỗi sang số
		if (checkCharPi(s.charAt(0))) return Math.PI; //charAt(0): Trả về kí tự tại vị trí 0 
		else return Double.parseDouble(s);
	}
	
	public boolean checkToanTu(char c){ 	// Kiểm tra toán tử
		// Liệt kê các toán tử
		char ToanTu[] = { '+', '-', '*', '/', 'b', '~', 's', '@', 't', 'c', '!', '%', ')', '('}; // ~ là dấu âm
		Arrays.sort(ToanTu); // Sắp xếp các toán tử
		if (Arrays.binarySearch(ToanTu, c) > -1) // Vị trí 0 là vị trí đầu tiên, toán tử phải đứng sau chữ số
			return true;
		else return false;
	}
	public int thuTuUuTien(char c){		// Thiết lập thứ tự ưu tiên
		switch (c) {
			case '+' : case '-' : return 1;
			case '*' : case '/' : return 2;
			case '~' : return 3;
			case '@' : case '!' : case 'b' : return 4;
			case 's' : case 'c' : case 't' : return 5;
		}
		// Thứ tự ưu tiên: 5 cao nhất, 1 thấp nhất
		return 0;
	}
	
	public boolean checkTT1Ngoi(char c){ 	// Kiểm tra toán tử 1 ngôi
		char ToanTu[] = { 's', 'c', 't', 'b', '@', '('};
		Arrays.sort(ToanTu);
		if (Arrays.binarySearch(ToanTu, c) > -1)
			return true;
		else return false;
	}

	public String chuanHoaBieuThuc(String s){ 
		String s1 = "";					// Tạo chuỗi s1
		// trim : Trả về bản sao của s1 nhưng không có khoảng trắng ở đầu và kết thúc chuỗi
		s = s.trim();
		// replaceAll : Xóa hết khoảng trắng trong s
		s = s.replaceAll("\\s+"," "); 	// Chuẩn hóa s
		int open = 0, close = 0;
		// 
		for (int i=0; i<s.length(); i++){
			char c = s.charAt(i); 
			if (c == '(') open++;
			if (c == ')') close++;
		}
		for (int i=0; i< (open - close); i++)
			s += ')';						// Thêm các dấu ")" vào cuối nếu thiếu
		for (int i=0; i<s.length(); i++){
			// Chuyển ...)(... thành ...)*(...
        	if (i>0 && checkTT1Ngoi(s.charAt(i)) && (s.charAt(i-1) == ')' || checkCharNumber(s.charAt(i-1))))
        		s1 = s1 + "*";
        	// Kiểm tra số âm
        	if ((i == 0 || (i>0 && !checkCharNumber(s.charAt(i-1)))) && s.charAt(i) == '-' && checkCharNumber(s.charAt(i+1))) {
        		s1 = s1 + "~"; 			
        	}
        	// VD 6π và ...)π chuyen sang 6*π và ...)*π
        	else if (i>0 && (checkCharNumber(s.charAt(i-1)) || s.charAt(i-1) == ')') && checkCharPi(s.charAt(i))){
        		s1 = s1 + "*" + s.charAt(i);
        	}
       		else s1 = s1 + s.charAt(i);
        }
		return s1;
	}
	// Xử lí biểu thức nhập vào thành các phần tử
	public String[] xuLyBieuThuc(String sMath){ 
		String s1 = "", phanTu[] = null;
		sMath = chuanHoaBieuThuc(sMath);
		Calculator  Cal = new Calculator();
		for (int i=0; i<sMath.length(); i++){
			char c = sMath.charAt(i);
			// Báo lỗi nếu có dạng π3
			if (i<sMath.length()-1 && checkCharPi(c) && !Cal.checkToanTu(sMath.charAt(i+1))){ 
				check_error = true;
				return null;
			}
			else 
				if (!Cal.checkToanTu(c))
					s1 = s1 + c;
				else s1 = s1 + " " + c + " ";
		}
		s1 = s1.trim();
		s1 = s1.replaceAll("\\s+"," "); 
		phanTu = s1.split(" "); 
		return phanTu;
	}
	
	public String[] postfix(String[] elementMath){
		Calculator Cal = new Calculator();
		String s1 = "", E[];
		Stack <String> S = new Stack<String>();
		for (int i=0; i<elementMath.length; i++){ 	
			char c = elementMath[i].charAt(0);		
			
			if (!Cal.checkToanTu(c)) 				
				s1 = s1 + elementMath[i] + " ";		
			else{									
				if (c == '(') S.push(elementMath[i]);	
				else{ 									
					if (c == ')'){						
						char c1;						
						do{
							c1 = S.peek().charAt(0);
							if (c1 != '(') s1 = s1 + S.peek() + " "; 	
							S.pop();
						}while (c1 != '(');
					}
					else{
						
						while (!S.isEmpty() && Cal.thuTuUuTien(S.peek().charAt(0)) >= Cal.thuTuUuTien(c)) 
							s1 = s1 + S.pop() + " ";
						S.push(elementMath[i]); 
					}
				}
			}
		}
		while (!S.isEmpty()) s1 = s1 + S.pop() + " "; 
		E = s1.split(" ");	
		return E;
	}
	//	Toán tử
	public String valueMath(String[] elementMath){
		Stack <Double> S = new Stack<Double>();
		Calculator Cal = new Calculator();
		double num = 0.0;
		for (int i=0; i<elementMath.length; i++){
			char c = elementMath[i].charAt(0);	
			if (checkCharPi(c)) S.push(Math.PI);	// Nếu là pi
			else{
				if (!Cal.checkToanTu(c)) S.push(Double.parseDouble(elementMath[i])); //so
				else{	// Nếu là toán tử
					
					double num1 = S.pop();
					switch (c) {
						case '~' : num = -num1; break;
						case 's' : num = Math.sin(num1); break;
						case 'c' : num = Math.cos(num1); break;
						case 't' : num = Math.tan(num1); break; 
						case '%' : num = num1/100; break;
						case 'b' : num = num1*num1; break;
						case '@' : {
							if (num1 >= 0){
								num = Math.sqrt(num1); break;
							}
							else check_error = true;
						}
						case '!' : {
							if (num1 >= 0 && (int)num1 == num1){
								num = 1;
								for (int j=1; j<=(int)num1; j++)
									num = num * j;
							}
							else check_error = true;
						}
						default : break;
					}
					if (!S.empty()){
						double num2 = S.peek();
						switch (c) {
						//-----------------------
							case '+' : num = num2 + num1; S.pop(); break;
							case '-' : num = num2 - num1; S.pop(); break;
							case '*' : num = num2 * num1; S.pop(); break;
							case '/' : {
								if (num1 != 0) num = num2 / num1;
								else check_error = true;
								S.pop(); break;
							}							
						}
					}
					S.push(num);
				}			
			}
		}
		return NumToString(S.pop());
	}
}


public class MainActivity extends Activity implements OnClickListener{
	String textMath = "", textAns = "0", screenTextMath = "";
	double num1 = 0, num2 = 0, ans = 0;
	char dau = ' ';
	int checkSubmit = 0;
	TextView screenAns, screenMath;
	Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDot, btnPi, btnAdd, btnMinus, btnMultiply, btnDived, btnSqrt, 
	btnPercent, btnInverse, btnResult, btnClearAll, btnClear, btnOpen, btnClose, btnSin, btnCos, btnTan, 
	btnFactorial, btnAbout, btnSqua, btnBack;
	
	public void Error(){
		screenAns.setText("Math Error!");
		textAns = textMath = screenTextMath = "";
	}
	public void submit(String[] elementMath){
		Calculator  Cal = new Calculator();
		if (textMath.length()>0){
			try{
				// Tách biểu thức thành các phần tử
				if (!Cal.check_error) elementMath = Cal.xuLyBieuThuc(textMath);
				// Đưa các phần tử về dạng postfix
				if (!Cal.check_error) elementMath = Cal.postfix(elementMath);
				// Lấy giá trị
				if (!Cal.check_error) textAns = Cal.valueMath(elementMath);		
				screenAns.setText(textAns);
				textMath = textAns;
				screenTextMath = textAns;
				checkSubmit = 1;	
			}catch(Exception e){
				Error();
			}
			if (Cal.check_error) Error();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		screenAns = (TextView) findViewById(R.id.screenAns);
		screenMath = (TextView) findViewById(R.id.screenMath);
		
		btn0 = (Button) findViewById(R.id.btn0);
		btn0.setOnClickListener(this);
		
		btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(this);
		
		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(this);
		
		btn3 = (Button) findViewById(R.id.btn3);
		btn3.setOnClickListener(this);
		
		btn4 = (Button) findViewById(R.id.btn4);
		btn4.setOnClickListener(this);
		
		btn5 = (Button) findViewById(R.id.btn5);
		btn5.setOnClickListener(this);
		
		btn6 = (Button) findViewById(R.id.btn6);
		btn6.setOnClickListener(this);
		
		btn7 = (Button) findViewById(R.id.btn7);
		btn7.setOnClickListener(this);
		
		btn8 = (Button) findViewById(R.id.btn8);
		btn8.setOnClickListener(this);
		
		btn9 = (Button) findViewById(R.id.btn9);
		btn9.setOnClickListener(this);
		
		btnDot = (Button) findViewById(R.id.btnDot);
		btnDot.setOnClickListener(this);
		
		btnPi = (Button) findViewById(R.id.btnPi);
		btnPi.setOnClickListener(this);
		
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		
		btnMinus = (Button) findViewById(R.id.btnMinus);
		btnMinus.setOnClickListener(this);
		
		btnSqua = (Button) findViewById(R.id.btnSqua);
		btnSqua.setOnClickListener(this);
		
		btnMultiply = (Button) findViewById(R.id.btnMul);
		btnMultiply.setOnClickListener(this);
		
		btnDived = (Button) findViewById(R.id.btnDiv);
		btnDived.setOnClickListener(this);
		
		btnPercent = (Button) findViewById(R.id.btnPer);
		btnPercent.setOnClickListener(this);
		
		btnSqrt = (Button) findViewById(R.id.btnSqrt);
		btnSqrt.setOnClickListener(this);
		
		btnInverse = (Button) findViewById(R.id.btnInver);
		btnInverse.setOnClickListener(this);
		
		btnOpen = (Button) findViewById(R.id.btnOpen);
		btnOpen.setOnClickListener(this);
		
		btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		
		btnSin = (Button) findViewById(R.id.btnSin);
		btnSin.setOnClickListener(this);
		
		btnCos = (Button) findViewById(R.id.btnCos);
		btnCos.setOnClickListener(this);
		
		btnTan = (Button) findViewById(R.id.btnTan);
		btnTan.setOnClickListener(this);
		
		btnFactorial = (Button) findViewById(R.id.btnFactor);
		btnFactorial.setOnClickListener(this);
		
		btnResult = (Button) findViewById(R.id.btnResult);
		btnResult.setOnClickListener(this);
		
		btnClearAll = (Button) findViewById(R.id.btnAC);
		btnClearAll.setOnClickListener(this);
		
		btnClear = (Button) findViewById(R.id.btnC);
		btnClear.setOnClickListener(this);	
		

		
		
	}
	
	@Override
    public void onClick(View v) { 
		int id = v.getId();
		String elementMath[] = null;
		// Thêm điều kiện: Độ dài biểu thức <48 kí tự
		if (id == R.id.btn0){
			if (screenTextMath.length()<48) {	
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "0";
				screenTextMath += "0";
			}			
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn1){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "1";
				screenTextMath += "1";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn2){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "2";
				screenTextMath += "2";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn3){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "3";
				screenTextMath += "3";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn4){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "4";
				screenTextMath += "4";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn5){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "5";
				screenTextMath += "5";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn6){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "6";
				screenTextMath += "6";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn7){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "7";
				screenTextMath += "7";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn8){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "8";
				screenTextMath += "8";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btn9){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "9";
				screenTextMath += "9";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnDot){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				if (textMath.equals("")) textMath += "0";
				textMath += ".";
				screenTextMath += ".";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnPi){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "π";
				screenTextMath += "π";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnAdd){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { checkSubmit = 0; }
				textMath += "+";
				screenTextMath += "+";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnMinus){
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { checkSubmit = 0; }
				textMath += "-";
				screenTextMath += "-";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnMul){
			if (screenTextMath.length()<48 && screenTextMath.length() > 0) {
				if (checkSubmit == 1) { checkSubmit = 0; }
				textMath += "*";
				screenTextMath += "*";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnDiv){
			if (screenTextMath.length()<48 && screenTextMath.length() > 0) {
				if (checkSubmit == 1) { checkSubmit = 0; }
				textMath += "/";
				screenTextMath += "/";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnSqua){ 
			if (screenTextMath.length()<48 && screenTextMath.length() > 0) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "b";
				screenTextMath += "²";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnSqrt){ 
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "@";
				screenTextMath +="√";
			}			
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnSin){ 
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "s(";
				screenTextMath +="Sin(";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnCos){ // cos
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "c(";
				screenTextMath +="Cos(";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnTan){ //tan
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "t(";
				screenTextMath +="Tan(";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnFactor){ //luy thua
			if (screenTextMath.length()<48) {
				if (checkSubmit == 1) { screenTextMath = textMath = ""; checkSubmit = 0; }
				textMath += "!";
				screenTextMath +="!";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnOpen){
			if (textMath.length()<48) {
				if (checkSubmit == 1) { checkSubmit = 0; }
				textMath += "(";
				screenTextMath +="(";
			}
			screenMath.setText(screenTextMath);
		}
		
		if (id == R.id.btnClose){
			if (textMath.length()<48 && textMath.length() > 0) {
				textMath += ")";
				screenTextMath +=")";
			}
			screenMath.setText(screenTextMath);
		}
		if (id == R.id.btnPer){ 	
			if (screenTextMath.length() == 0) screenTextMath = "0";
			screenTextMath = "(" + screenTextMath + ")%";
			screenMath.setText(screenTextMath);			
			if (checkSubmit == 0) submit(elementMath);
			textMath = textAns + "/100";
			submit(elementMath);
		}
		if (id == R.id.btnInver){ 	
			if (screenTextMath.length() == 0) screenTextMath = "0";
			screenTextMath = "1/(" + screenTextMath + ")";
			screenMath.setText(screenTextMath);		
			if (checkSubmit == 0) submit(elementMath);
			textMath = "1/" + textAns;
			submit(elementMath);
		}
		if (id == R.id.btnResult){
			submit(elementMath);
		}
		if (id == R.id.btnAC){
			textMath = "";
			screenTextMath = "";
			textAns = "0";
			screenAns.setText(textAns);
			screenMath.setText("|");		
		}
		if (id == R.id.btnC){
			if (screenMath.length()>0){
				char c = textMath.charAt(textMath.length()-1);
				if (textMath.length() > 1 && c == '(' && textMath.charAt(textMath.length()-2) == '^'){
					screenTextMath = screenTextMath.substring(0,screenTextMath.length()-2);
					textMath = textMath.substring(0,textMath.length()-2);
				}
				else if (textMath.length() > 1 && c == '(' && (textMath.charAt(textMath.length()-2) == 's' || textMath.charAt(textMath.length()-2) == 'c' || textMath.charAt(textMath.length()-2) == 't') ){
					textMath = textMath.substring(0,textMath.length()-2);
					screenTextMath = screenTextMath.substring(0,screenTextMath.length()-4);
				}
				else {
					textMath = textMath.substring(0,textMath.length()-1);
					screenTextMath = screenTextMath.substring(0,screenTextMath.length()-1);
				}
			}
			screenMath.setText(screenTextMath);	
		}
	}
}
