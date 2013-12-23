package com.compuality

import groovy.transform.AutoClone

@AutoClone
public class CPU {
	public static final int INST_LD    = 0;
	public static final int INST_LD_L  = 1;
	public static final int INST_STR   = 2;
	public static final int INST_STR_P = 3;
	public static final int INST_ADD   = 4;
	public static final int INST_ADD_L = 5;
	public static final int INST_SUB   = 6;
	public static final int INST_SUB_L = 7;
	public static final int INST_INC   = 8;
	public static final int INST_INC_P = 9;
	public static final int INST_DEC   = 10;
	public static final int INST_DEC_P = 11;
	public static final int INST_BNZ   = 12;
	public static final int INST_BNZ_L = 13;
	public static final int INST_BRA   = 14;
	public static final int INST_BRA_L = 15;

	public static final int NUM_INST_BITS = 4;
	public static final int NUM_INST_UNITS = 1 << NUM_INST_BITS;

	public int INST_MASK;

	public int ADDY_BITS;
	public int ADDY_UNITS;
	public int ADDY_MASK;

	public int WORD_BITS;
	public int WORD_SIZE;
	public int WORD_MASK;

	public int[] mem;

	private int pc = 0;
	private int d0 = 0;

  public CPU() {}

	public CPU(int numAddyBits) {
		ADDY_BITS = numAddyBits;
		ADDY_UNITS = 1 << ADDY_BITS;
		ADDY_MASK = ADDY_UNITS-1;

		INST_MASK = (NUM_INST_UNITS-1) << ADDY_BITS;

		WORD_BITS = NUM_INST_BITS + ADDY_BITS;
		WORD_SIZE = 1 << WORD_BITS;
		WORD_MASK = WORD_SIZE-1;

		mem = new int[ADDY_UNITS];
	}

	public void reset() {
		pc = 0;
		d0 = 0;
	}

	public void setPC(int pc) {
		this.pc = pc & ADDY_MASK;
	}

	public void setD0(int d0) {
		this.d0 = d0 & WORD_MASK;
	}

	public void setMem(int addy, int val) {
		mem[addy & ADDY_MASK] = val & WORD_MASK;
	}

	public int getMem(int addy) {
		return mem[addy & ADDY_MASK];
	}

	public void sim(int numTicks) {
		for(int i=0; i < numTicks; ++i) {
			int ir = mem[pc];
			setPC(pc+1);
			int inst = (ir & INST_MASK) >> ADDY_BITS;
			int arg = ir & ADDY_MASK;

			switch(inst) {
				case INST_LD:
					setD0(getMem(arg));
					break;

				case INST_LD_L:
					setD0(arg);
					break;

				case INST_STR:
					setMem(arg, d0);
					break;

				case INST_STR_P:
					setMem(getMem(arg), d0);
					break;

				case INST_ADD:
					setD0(d0 + getMem(arg));
					break;

				case INST_ADD_L:
					setD0(d0 + arg);
					break;

				case INST_SUB:
					setD0(d0 - getMem(arg));
					break;

				case INST_SUB_L:
					setD0(d0 - arg);
					break;

				case INST_INC:
					setMem(arg, getMem(arg)+1);
					break;

				case INST_INC_P:
					int addy = getMem(arg);
					setMem(addy, getMem(addy)+1);
					break;

				case INST_DEC:
					setMem(arg, getMem(arg)-1);
					break;

				case INST_DEC_P:
					int addy = getMem(arg);
					setMem(addy, getMem(addy)-1);
					break;

				case INST_BNZ:
					if(d0 != 0)
						setPC(getMem(arg));
					break;

				case INST_BNZ_L:
					if(d0 != 0)
						setPC(arg);
					break;

				case INST_BRA:
					setPC(getMem(arg));
					break;
				
				case INST_BRA_L:
					setPC(arg);
					break;

				default:
					throw new RuntimeException("Instruction not found: " + inst);
			}
		}
	}

	public void randMem() {
		Random rand = new Random();
		for(int i=0; i < ADDY_UNITS; ++i) {
			setMem(i, rand.nextInt());
		}
	}

	public void printMem() {
		System.out.printf("d0=%3x mem=", d0);
		for(int m=0; m < ADDY_UNITS; ++m)
			System.out.printf("%3x", getMem(m));
		System.out.println();
	}

	public boolean equals(Object obj) {
		if(obj instanceof CPU) {
			CPU other = (CPU)obj;
			return Arrays.equals(mem, other.mem);
		}	
		return false;
	}

	public int hashCode() {
		return Arrays.hashCode(mem);
	}

	public static void main(String[] args) {
		//seriesTest();
		solutionTest();
	}

	public static void seriesTest() {
		CPU cpu = new CPU(4);
		cpu.mem[0] = 0x10;
		cpu.mem[1] = 0x28;
		cpu.mem[2] = 0x80;
		cpu.mem[3] = 0x81;
		cpu.mem[4] = 0x77;
		cpu.mem[5] = 0xD0;
		cpu.mem[6] = 0xF6;

		cpu.sim(100);
		cpu.printMem();
	}

	public static void solutionTest() {
		CPU cpu = new CPU(4);
		cpu.mem[0] = 0x27;
		cpu.mem[1] = 0x28;
		cpu.mem[2] = 0x81;
		cpu.mem[3] = 0xE4;
		cpu.mem[4] = 0xF5;
		cpu.mem[5] = 0x6F;
		cpu.mem[6] = 0xE2;
		cpu.mem[7] = 0x3D;
		
		cpu.mem[8] = 0xFF;
		cpu.mem[9] = 0xFF;
		cpu.mem[10] = 0xFF;
		cpu.mem[11] = 0xFF;
		cpu.mem[12] = 0xFF;
		cpu.mem[13] = 0xFF;
		cpu.mem[14] = 0xFF;
		cpu.mem[15] = 0xFF;

		for(int i=0; i < 256; ++i) {
			cpu.sim(1);
			cpu.printMem();
		}
	}
}
