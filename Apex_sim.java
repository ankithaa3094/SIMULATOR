import java.io.*;
import java.util.*;

class global 
{
	//change the directory of input file accordingly
	static String filename 				= "/home/pradeepsai/workspace/APEX/easy1.txt" ;
	static int num_of_instructions 		= 1000;
	static int num_of_clock_cycles 		= 1000;
	static int clock 					= 0;
	static int initial_address ;
	static int temp_dest_index 			= 0;
	static int loop;
	static int zero_flag		=0;//to check for bz and bnz conditions
	static int bz_taken			=0;//tocheck if branch if zero is taken or not
	static int bnz_taken		=0;
	static int count_inputtimes =0;//taking user input into this file and reset it to zero when initializing.
	static int is_decode_free   =1;//checking if decode is free to send next instruction
	static int is_fetch_free	=1;//To check if the fetch is free
	
	static Queue<Integer> temp_dest						= new LinkedList<Integer>();//may coz problem for out of order execution.
	static int[][] ganttchart							= new int[6][1000];
	static int[] memory_storage							= new int[10000];//Linear array of memory
	static int[] valid_stages 							= new int[5];//check if the stage is free or not.
	static int[] pc_stage	  							= new int[5]; //to store the pc value for each stage.
	static int[][] result								= new int[6][num_of_clock_cycles];
	static Hashtable<String, Integer> registers			= new Hashtable<String, Integer>();//for storing the values in registers.
	static Hashtable<String, Integer> dest_valid 		= new Hashtable<String, Integer>();
	static Hashtable<String, Integer> dest_valid_clock	= new Hashtable<String, Integer>();
	
	//To store the contents of the instruction.
	static String[] obj_instruction 	 = new String[num_of_instructions];
	static String[] obj_type			 = new String[num_of_instructions];
	static String[] obj_destination		 = new String[num_of_instructions];
	static String[] obj_source1 	   	 = new String[num_of_instructions];
	static String[] obj_source2 		 = new String[num_of_instructions];
	static Integer[] obj_literal1		 = new Integer[num_of_instructions];
	static Integer[] obj_literal2 		 = new Integer[num_of_instructions];
	
	public static int getindex(int program_counter)
	{
		return program_counter-20000+1;
	}
	
	public static void makeinvalid(String register)
	{
		dest_valid.put(register,0);//0 is invalid
		dest_valid_clock.put(register,clock);//updating in which clock cycle it's invalid. not mandatory though
	}
	
	public static void makevalid(String register)
	{
		dest_valid.put(register,1);//0 is invalid
		dest_valid_clock.put(register,clock);//updating in which clock cycle it's invalid. not mandatory though
	}
	
	public static void lockallstages()
	{
		valid_stages[0]=0;valid_stages[1]=0;valid_stages[2]=0;
		valid_stages[3]=0;valid_stages[4]=0;
	}
	
	public static void display()
	{
		for(int i=0;i<100;i++)
		{
			
			System.out.format("%2d---->%5d\n",i,memory_storage[i]);
		}
		System.out.println("");
		
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<loop;j++)
			{
				System.out.format("%4d",ganttchart[i][j]);
			}
			System.out.println(" ");
		}
		System.out.println(registers);
	}
	
	
	public static void setallpc(int setpc)
	{
		pc_stage[0] = setpc;pc_stage[1] = setpc;pc_stage[2] = setpc;pc_stage[3] = setpc;pc_stage[4] = setpc;
	}
	public static void initialize()
	{
		global.initial_address   =20000;
		global.valid_stages[0]   = 1;//making fetch valid
		global.pc_stage[0] 		 = 20000;//specifying pc value for fetch stage
		global.pc_stage[1] 		 = 20000;
		global.pc_stage[2] 		 = 20000;
		global.pc_stage[3] 		 = 20000;
		global.pc_stage[4] 		 = 20000;
		global.count_inputtimes	 = 0;
		
		
		
		//initialziing values in register
		registers.put("R0", 0);registers.put("R1", 0);registers.put("R2", 0);registers.put("R3", 0);
		registers.put("R4", 0);registers.put("R5", 0);registers.put("R6", 0);registers.put("R7", 0);
		
		//initializeing all registers to be valid at the 0th clock cycle, 1 -indicating valid
		dest_valid.put("RO", 1);dest_valid.put("R2", 1);dest_valid.put("R4", 1);dest_valid.put("R6", 1);
		dest_valid.put("R1", 1);dest_valid.put("R3", 1);dest_valid.put("R5", 1);dest_valid.put("R7", 1);
		
		dest_valid_clock.put("RO", 0);dest_valid_clock.put("R2", 0);dest_valid_clock.put("R4", 0);dest_valid_clock.put("R6", 0);
		dest_valid_clock.put("R1", 0);dest_valid_clock.put("R3", 0);dest_valid_clock.put("R5", 0);dest_valid_clock.put("R7", 0);
		
		zero_flag		=0;//to check for bz and bnz conditions
		bz_taken			=0;//tocheck if branch if zero is taken or not
		bnz_taken		=0;
		count_inputtimes =0;//taking user input into this file and reset it to zero when initializing.
		is_decode_free   =1;//checking if decode is free to send next instruction
		is_fetch_free	=1;//To check if the fetch is free
	}
	public static void disp_register()
	{
		System.out.println(registers);
	}
}

class fetch extends global 
{
//To fetch the instruction based on the program counter value specified.
	static void fetch_instruction()
	{
		
		if(is_decode_free == 1)
		{
		try
		{
			ganttchart[0][loop]= getindex(pc_stage[0]);
		
			FileReader filereader = new FileReader(filename);
			BufferedReader bufferedreader = new BufferedReader(filereader);
			int index = initial_address;
			String instruction = new String();
						
			while((instruction=bufferedreader.readLine())!=null && index < pc_stage[0])
			{
				index++;
			}
			bufferedreader.close();
		
			if(instruction==null)
			{
				System.out.println("Read null from file");
				valid_stages[0] = 0;
			}
			else
			{
				obj_instruction[getindex(pc_stage[0])] = instruction;
				valid_stages[1] = 1;
				pc_stage[0]++;
			}	
			
		}
		
		catch(Exception e)
		{
			System.out.println("Error opening the file");
		}
		}
		else
			ganttchart[0][loop]= getindex(pc_stage[0]);
		
	}
}
class decode extends global
{
	static boolean dependency_exists_source1()
	{

		if(obj_source1[getindex(pc_stage[1])]!=null && registers.containsKey(obj_source1[getindex(pc_stage[1])]) )
		{
			if(dest_valid.get(obj_source1[getindex(pc_stage[1])])==0 )//invalid value in register
			{
				
				valid_stages[2]=0;
				is_decode_free=0;
				return true;//sending true if dependency exists.
			}
			else if(dest_valid.get(obj_source1[getindex(pc_stage[1])])==1 && dest_valid_clock.get(obj_source1[getindex(pc_stage[1])]) == loop)
			{
				//unlocking the next stages since the data is valid it will be available in next cycle
				valid_stages[2]=1;
				is_decode_free=1;
				pc_stage[1]++; //decode takes in the next instruction ans starts processing it.
				return true;//data forwarding like case. just for this part don't use even if it's valid
			}
			else//valid bit is 1 and it was valid before.
			{
				//no dependecy exists in this case.
				return false;
			}
		}
		return false;
	}
	static boolean dependency_exists_source2()
	{
		//for load instruction obj_source2 doesn't contain string array
		if(obj_source2[getindex(pc_stage[1])]!=null && registers.containsKey(obj_source2[getindex(pc_stage[1])]))
		{
			if(dest_valid.get(obj_source2[getindex(pc_stage[1])])==0 )//invalid value in register
			{
				valid_stages[2]=0;
				is_decode_free=0;
				return true;//sending true if dependency exists.
			}
			else if(dest_valid.get(obj_source2[getindex(pc_stage[1])])==1 && dest_valid_clock.get(obj_source2[getindex(pc_stage[1])])==loop)
			{
				valid_stages[2]=1;
				is_decode_free=1;
				pc_stage[1]++;
				return true;//data forwarding like case. just for this part don't use even if it's valid
			}
			else
			{
				
				//valid bit is 1 and it's not valid in current clock cycle
				return false;
			}
			
		}
		return false;//if no sources are having value in it then it's a literal operation
	}
	
	static void decode_instruction()
	{
		try
		{
			ganttchart[1][loop]= getindex(pc_stage[1]);
			String[] temp ;
			temp = obj_instruction[getindex(pc_stage[1])].split(" ");
			obj_type[getindex(pc_stage[1])]=temp[0];
	
			switch(obj_type[getindex(pc_stage[1])])
			{
				case "BAL"	: obj_source1[getindex(pc_stage[1])]	=temp[1];
				  			  obj_literal1[getindex(pc_stage[1])]	=Integer.parseInt(temp[2]);
				  			  break;
				case "MOVC" : obj_destination[getindex(pc_stage[1])]	=temp[1];
							  obj_literal1[getindex(pc_stage[1])]		=Integer.parseInt(temp[2]);
							  break;
				case "MOV" : obj_destination[getindex(pc_stage[1])]	=temp[1];
							 if(registers.containsKey(temp[2])){
								 obj_source1[getindex(pc_stage[1])]		=temp[2];}
							 else
								 obj_literal1[getindex(pc_stage[1])]	=Integer.parseInt(temp[2]);
				  			 break;
				case "ADD"  : 
				case "SUB"  : 			
				case "MUL"	: 
				case "AND"	: 
				case "OR" 	: 
				case "EX-OR": obj_destination[getindex(pc_stage[1])]	=temp[1];
							  obj_source1[getindex(pc_stage[1])]		=temp[2];
							  obj_source2[getindex(pc_stage[1])]		=temp[3];
							  break;
				case "LOAD" :
				case "STORE": obj_destination[getindex(pc_stage[1])]	=temp[1];
							  obj_source1[getindex(pc_stage[1])]		=temp[2];
							  obj_source2[getindex(pc_stage[1])]		=temp[3];
				/*			To generalize for next part
				 * 			  if(registers.containsKey(temp[3])){
									 obj_source2[getindex(pc_stage[1])]		=temp[3];}
								 else
									 obj_literal1[getindex(pc_stage[1])]	=Integer.parseInt(temp[3]);*/
							  break;
				case "BZ"	: 
				case "BNZ"	: obj_literal1[getindex(pc_stage[1])]		=Integer.parseInt(temp[1]);
							  break;	
				case "JUMP" : obj_source1[getindex(pc_stage[1])]		=temp[1];
				  			  obj_literal1[getindex(pc_stage[1])]		=Integer.parseInt(temp[2]);
				  			  System.out.println(obj_source1[getindex(pc_stage[1])]+"-------"+ obj_literal1[getindex(pc_stage[1])]);
							  break;
				case "HALT" : //stop executing the instructions.
								valid_stages[0]=0;valid_stages[1]=1;
							  return;
				default		: System.out.println(obj_type[getindex(pc_stage[1])]+"blah");
							  System.out.print("Invalid instruction type while decoding");
							  break;
			}
	//		if(valid_stages[0]==0) valid_stages[1]=0;
		
		}
		
		catch(Exception e)
		{
			System.out.println("Error decoding the file");
		}
		
		if(dependency_exists_source1())
		{
		
		}
		else if (dependency_exists_source2())
		{
			
		}
		else
		{
			is_decode_free = 1;
			pc_stage[1]++;
			valid_stages[2]=1;
		}	
	}
	
}

class execute extends global
{
	int temp;
	
	static void execute_instruction()
	{
		//System.out.println(pc_stage[2]);
	try	
	{	
		ganttchart[2][loop]= getindex(pc_stage[2]);
		//not handling dependency for load instructions.
		if(obj_type[getindex(pc_stage[2])].matches("ADD|SUB|MUL|MOV|MOVC|AND|OR|EX-OR"))
		{

			dest_valid.put(obj_destination[getindex(pc_stage[2])], 0);//making them invalid 0 for invalid
	
		}
		switch(obj_type[getindex(pc_stage[2])])
		{
				case "ADD": temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])] ) + registers.get(obj_source2[getindex(pc_stage[2])]));
							break;
							
				case "SUB": zero_flag = (registers.get(obj_source1[getindex(pc_stage[2])]) - registers.get(obj_source2[getindex(pc_stage[2])]));
							temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]) - registers.get(obj_source2[getindex(pc_stage[2])]));
							
							break;
							
				case "MOVC":temp_dest.add(obj_literal1[getindex(pc_stage[2])]);
							break;
							
				case "MOV":if(obj_source1[getindex(pc_stage[2])]!=null)
								temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]));
						   else
							   temp_dest.add(obj_literal1[getindex(pc_stage[2])]);
							break;
					
				case "MUL": temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]) * registers.get(obj_source2[getindex(pc_stage[2])]));
							break;
							
				case "AND": temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]) & registers.get(obj_source2[getindex(pc_stage[2])]));
							break;
							
				case "OR":  temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]) | registers.get(obj_source1[getindex(pc_stage[2])]));
							break;
							
				case "EX-OR": temp_dest.add(registers.get(obj_source1[getindex(pc_stage[2])]) ^ registers.get(obj_source1[getindex(pc_stage[2])]));
							break;
				case "LOAD" ://To differentiate between operands and literal values.
				case "STORE":if(registers.containsKey(obj_source2[getindex(pc_stage[2])]))
								obj_literal2[getindex(pc_stage[2])] = registers.get(obj_source1[getindex(pc_stage[2])] ) + registers.get(obj_source2[getindex(pc_stage[2])]);
							 else
								 obj_literal2[getindex(pc_stage[2])] = registers.get(obj_source1[getindex(pc_stage[2])] ) + Integer.parseInt((obj_source2[getindex(pc_stage[2])]));
							break;
				case "BAL" : registers.put("X",(pc_stage[2]+1) );			
							 obj_literal2[getindex(pc_stage[2])]=(pc_stage[2]+registers.get(obj_source1[getindex(pc_stage[2])])+obj_literal1[getindex(pc_stage[2])]);
							break;
				case "BZ"	:if(zero_flag==0)
							{	
							
								obj_literal2[getindex(pc_stage[2])] = pc_stage[2] + obj_literal1[getindex(pc_stage[2])];
								bz_taken =1;
							}
							else 
								bz_taken = 0;
							break;	
				case "BNZ"	:if(zero_flag!=0)
								{	
									
									obj_literal2[getindex(pc_stage[2])] = pc_stage[2] + obj_literal1[getindex(pc_stage[2])];
									bnz_taken =1;
								}
							else 
									bnz_taken = 0;
							break;
				case "JUMP" :
							obj_literal2[getindex(pc_stage[2])] = registers.get(obj_source1[getindex(pc_stage[2])] ) + obj_literal1[getindex(pc_stage[2])];
							System.out.println(obj_literal2[getindex(pc_stage[2])]+"<<-----");
							break;
				default:	System.out.print("Invalid instruction type");
							break;
		}
		//if(valid_stages[1]==0) valid_stages[2]=0;
			pc_stage[2]++;
			valid_stages[3]=1;
	}
	catch(Exception e)
	{
		System.out.println("Error executing the instrucion ");
	}
	}
}


class memory extends global
{
	static void memory_instruction()
	{
		try
		{
			ganttchart[3][loop]= getindex(pc_stage[3]);
			switch(obj_type[getindex(pc_stage[3])])
			{
					case "LOAD":
								temp_dest.add(memory_storage[obj_literal2[getindex(pc_stage[3])]]);
								break;
					case "STORE":
								memory_storage[obj_literal2[getindex(pc_stage[3])]] = registers.get(obj_destination[getindex(pc_stage[3])]);
								break;
					case "BZ"   : if(bz_taken == 1)
								{
									 setallpc(obj_literal2[getindex(pc_stage[3])]);
									 valid_stages[4] = 0;
									 valid_stages[1] = 0;
									 valid_stages[2] = 0;
									 valid_stages[3] = 0;
									 return;	
								}
									else
										break;
					case "BNZ"   : if(bnz_taken == 1)
									{
										
						 				setallpc(obj_literal2[getindex(pc_stage[3])]);
						 				valid_stages[4] = 0;
						 				valid_stages[1] = 0;
						 				valid_stages[2] = 0;
						 				valid_stages[3] = 0;
						 				return;	
									}
									else
								break;
					case "BAL"	:	setallpc(obj_literal2[getindex(pc_stage[3])]);
									valid_stages[4] = 0;
									valid_stages[1] = 0;
									valid_stages[2] = 0;
									valid_stages[3] = 0;
								return;	
					case "JUMP": setallpc(obj_literal2[getindex(pc_stage[3])]);
								 valid_stages[4] = 0;
								 valid_stages[1] = 0;
								 valid_stages[2] = 0;
								 valid_stages[3] = 0;
						
								 return;
					default:
			//			System.out.println("Memory" + getindex(pc_stage[3]) );
			}
		//if(valid_stages[2]==0) valid_stages[3]=0;
		pc_stage[3]++;
		valid_stages[4]=1;
		}
	
		catch(Exception e)
		{
			System.out.println("Exception caught in mem stage");
		}
	}
}

class writeBack extends global
{
	static void writeBack_instruction()
	{
		try
		{
			ganttchart[4][loop]= getindex(pc_stage[4]);
			switch(obj_type[getindex(pc_stage[4])])
			{
					case "STORE": 
					case  "JUMP": 
					case "BZ"	:
					case "BAL"	:
					case "BNZ"	:break;
					default : registers.put(obj_destination[getindex(pc_stage[4])],temp_dest.poll());
								dest_valid.put(obj_destination[getindex(pc_stage[4])],1);
								dest_valid_clock.put(obj_destination[getindex(pc_stage[4])], clock);
							  
			}
			//System.out.println(registers);
			pc_stage[4]++;
			//if(valid_stages[3]==0) valid_stages[4]=0;
		}
		catch(RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println("Exception caught in WriteBack");
		}
	}
}

public class Apex_sim extends global
	{
		
		public static void main(String args[])
		{
			int simulate = 0;
			String input = new String();
			String[] temp;
			initialize();
			System.out.println("Enter 1.simulate n 2.initialize 3.exit");
			Scanner in = new Scanner(System.in);

			while(true)
			{
				input = in.nextLine();
				
				if(input.equals("display"))
					display();
				else if (input.equals("initialize"))
					initialize();
				else if(input.equals("exit"))
					System.exit(0);
				else
				{
				
					temp 	 = input.split(" ");
					if (temp[0].equals("simulate") == false)
					{
						System.out.println("invalid input try again.");
						continue;
					}
					count_inputtimes = Integer.parseInt(temp[1]);
					simulate += global.count_inputtimes;
					initialize();
					for( loop=0;loop<simulate;loop++)
					{
						if(valid_stages[4]==1)
							writeBack.writeBack_instruction();
				
						if(valid_stages[3]==1)
							memory.memory_instruction();
						else
							valid_stages[4]=0;
				
						if(valid_stages[2]==1)
							execute.execute_instruction();
						else
							valid_stages[3]=0;
				
						if(valid_stages[1]==1)
							decode.decode_instruction();
						else
							valid_stages[2]=0;
				
						if(valid_stages[0]==1)
							fetch.fetch_instruction();
						else
							valid_stages[1]=0;
					}
					}
			}
			
		}

	}
