/*
 * Copyright 2010 Tom Gibara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package am.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * <p>
 * Stores fixed-length bit sequences of arbitrary length and provides a number
 * of bit-wise operations, and methods for exposing the bit sequences as
 * established java types.
 * </p>
 * 
 * <p>
 * In keeping with Java standards, bits are operated-on and exposed as
 * <em>big-endian</em>. This means that, where bit sequences are input/output
 * from methods, the least-significant bit is always on the right and the most
 * significant bit is on the left. So, for example, the {@link #toString()}
 * method will contain the most significant bit in the character at index 0 in
 * the string. Naturally, In the cases where this class is used without
 * externalizing the bit representation, this distinction is irrelevant.
 * </p>
 * 
 * <p>
 * A consequence of this is that, in methods that are defined over ranges of
 * bits, the <em>from</em> and <em>to</em> parameters define the rightmost and
 * leftmost indices respectively. As per Java conventions, all <em>from</em>
 * parameters are inclusive and all <em>to</em> parameters are exclusive.
 * </p>
 * 
 * <p>
 * Instances of this class may be aligned (see {@link #isAligned()} and
 * {@link #aligned()}. Many operations will execute more efficiently on aligned
 * instances. Instances may also be immutable (see {@link #isMutable()},
 * {@link #mutable()} and {@link #immutable()}). In addition to a number of
 * copying operations, the class also supports views; views create new
 * {@link BitVector} instances that are backed by the same bit data, this allows
 * applications to expose mutable {@link BitVector} instances 'safely' via
 * immutable views.
 * </p>
 * 
 * <p>
 * The class extends {@link Number} which allows it to be treated as an extended
 * length numeric type (albeit, one that doesn't support any arithmetic
 * operations). In addition to the regular number value methods on the
 * interface, a {@link #toBigInteger()} method is available that returns the
 * BitVector as a positive, arbitrarily sized integer. Combined with the
 * fromBigInteger() method, this allows, with some loss of performance, a range
 * of arithmetic calculations to be performed.
 * </p>
 * 
 * <p>
 * The class also implements {@link Iterable} and provides methods to obtain
 * {@link ListIterator} as one would for a {@link List}, but stops short of
 * implementing the {@link List} interface. This allows a {@link BitVector} to
 * be used with a range of Java language constructs and standard library
 * classes.
 * </p>
 * 
 * <p>
 * The class is {@link Serializable} and {@link Cloneable} too (clones are
 * shallow and essentially behave as a view of the original instance). For
 * instances where more control over serialization is needed,
 * {@link #read(InputStream)} and {@link #write(OutputStream)} methods are
 * available, though better performance may result from calling
 * {@link #toByteArray()} and managing the writing outside this class.
 * </p>
 * 
 * <p>
 * In addition to all of the above methods outlined above, a full raft of
 * bitwise operations are available, including:
 * </p>
 * 
 * <ul>
 * <li>set, and, or, xor operations over a variety of inputs</li>
 * <li>shifts, rotations and reversals</li>
 * <li>tests for bit-wise intersection, containment and equality</li>
 * <li>tests for all-zero and all-one ranges</li>
 * <li>counting the number ones/zeros in a range</li>
 * <li>searches for first/last ones/zeros in a given range</li>
 * <li>searches for next/previous ones/zeros from a given index</li>
 * <li>copying and viewing bit ranges</li>
 * <li>obtaining bits as Java primitive types</li>
 * </ul>
 * 
 * <p>
 * Most such methods are available in both an operation specific version and
 * operation parameterized version to cater for different application needs.
 * </p>
 * 
 * <p>
 * Performance should be adequate for most uses. There is scope to improve the
 * performance of many methods, but none of the methods operate in anything more
 * than linear time and inner loops are mostly 'tight'. An implementation detail
 * (which may be important on some platforms) is that, with some exceptions,
 * none of the methods perform any object creation. The exceptions are: methods
 * that require an object to be returned, {@link #floatValue()},
 * {@link #doubleValue()}, {@link #toString(int)}, and situations where an
 * operation is applied with overlapping ranges of bits, in which case it may be
 * necessary to create a temporary copy of the {@link BitVector}.
 * </p>
 * 
 * <p>
 * The class is marked as final to ensure that immutable instances can be safely
 * used in security sensitive code (eg. within a {@link PrivilegedAction}).
 * </p>
 * 
 * @author Tom Gibara
 * 
 */

public final class BitVector extends Number implements Cloneable, Iterable<Boolean> {

	// statics
	
	public enum Operation {
		SET,
		AND,
		OR,
		XOR
	}

	public enum Comparison {
		EQUALS,
		INTERSECTS,
		CONTAINS
	}

	public static final BitVector fromBigInteger(BigInteger bigInt) {
		if (bigInt == null) throw new IllegalArgumentException();
		if (bigInt.signum() < 0) throw new IllegalArgumentException();
		final int length = bigInt.bitLength();
		return fromBigIntegerImpl(bigInt, length, length);
	}
	
	public static BitVector fromBigInteger(BigInteger bigInt, int size) {
		if (bigInt == null) throw new IllegalArgumentException();
		if (bigInt.signum() < 0) throw new IllegalArgumentException();
		if (size < 0) throw new IllegalArgumentException();
		final int length = Math.min(size, bigInt.bitLength());
		return fromBigIntegerImpl(bigInt, size, length);

	}
	
	public static BitVector fromByteArray(byte[] bytes, int size) {
		//TODO provide a more efficient implementation
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		if (size < 0) throw new IllegalArgumentException("negative size");
		BigInteger bigInt = new BigInteger(1, bytes);
		final int length = Math.min(size, bigInt.bitLength());
		return fromBigIntegerImpl(bigInt, size, length);
	}
	
	private static BitVector fromBigIntegerImpl(BigInteger bigInt, int size, int length) {
		final BitVector vector = new BitVector(size);
		final long[] bits = vector.bits;
		long v = 0L;
		int i = 0;
		for (; i < length; i++) {
			if (bigInt.testBit(i)) {
				v = (v >>> 1) | Long.MIN_VALUE;
			} else {
				v >>>= 1;
			}
			if ((i & ADDRESS_MASK) == ADDRESS_MASK) {
				bits[i >> ADDRESS_BITS] = v;
				v = 0;
			}
		}
		if (v != 0L) bits[i >> ADDRESS_BITS] = v >>> (ADDRESS_SIZE - (i & ADDRESS_MASK));
		return vector;
	}
	
	public static BitVector fromBitSet(BitSet bitSet) {
		if (bitSet == null) throw new IllegalArgumentException();
		final int length = bitSet.length();
		return fromBitSetImpl(bitSet, length, length);
	}

	public static BitVector fromBitSet(BitSet bitSet, int size) {
		if (bitSet == null) throw new IllegalArgumentException();
		if (size < 0) throw new IllegalArgumentException();
		final int length = Math.min(size, bitSet.length());
		return fromBitSetImpl(bitSet, size, length);
	}

	private static BitVector fromBitSetImpl(BitSet bitSet, int size, int length) {
		final BitVector vector = new BitVector(size);
		final long[] bits = vector.bits;
		long v = 0L;
		int i = 0;
		for (; i < length; i++) {
			if (bitSet.get(i)) {
				v = (v >>> 1) | Long.MIN_VALUE;
			} else {
				v >>>= 1;
			}
			if ((i & ADDRESS_MASK) == ADDRESS_MASK) {
				bits[i >> ADDRESS_BITS] = v;
				v = 0;
			}
		}
		if (v != 0L) bits[i >> ADDRESS_BITS] = v >>> (ADDRESS_SIZE - (i & ADDRESS_MASK));
		return vector;
	}

	private static final int SET = 0;
	private static final int AND = 1;
	private static final int OR  = 2;
	private static final int XOR = 3;

	private static final int EQUALS = 0;
	private static final int INTERSECTS = 1;
	private static final int CONTAINS = 2;
	
	private static final int ADDRESS_BITS = 6;
	private static final int ADDRESS_SIZE = 1 << ADDRESS_BITS;
	private static final int ADDRESS_MASK = ADDRESS_SIZE - 1;
	
	private boolean overlapping(int thisFrom, int thisTo, int thatFrom, int thatTo) {
		return thisTo > thatFrom && thisFrom < thatTo;
	}

	//necessary for throwing an IAE
	private static int stringLength(String str) {
		if (str == null) throw new IllegalArgumentException();
		return str.length();
	}

	//duplicated here to avoid dependencies
	private static int gcd(int a, int b) {
		while (a != b) {
			if (a > b) {
				int na = a % b;
				if (na == 0) return b;
				a = na;
			} else {
				int nb = b % a;
				if (nb == 0) return a;
				b = nb;
			}
		}
		return a;
	}

	//used to avoid direct dependence on Java6 methods
	private final static ArrayCopier copier;
	
	static {
		boolean canCopyRanges;
		try {
			Arrays.class.getMethod("copyOfRange", (new long[0]).getClass(), Integer.TYPE, Integer.TYPE);
			canCopyRanges = true;
		} catch (NoSuchMethodException e) {
			canCopyRanges = false;
		}
		copier = canCopyRanges ? new RangeCopier() : new SystemCopier();
	}
	
	// fields
	
	//core fields
	private final int start;
	private final int finish;
	private final long[] bits;
	private final boolean mutable;
	
	// constructors
	
	//creates a new bit vector of the specified size
	//naturally aligned
	public BitVector(int size) {
		if (size < 0) throw new IllegalArgumentException();
		if (size > (Integer.MAX_VALUE / 8)) throw new IllegalArgumentException();
		final int length = (size + ADDRESS_MASK) >> ADDRESS_BITS;
		this.bits = new long[length];
		this.start = 0;
		this.finish = size;
		this.mutable = true;
	}
	
	//TODO consider changing String constructors to static methods
	//creates a new bit vector from the supplied binary string
	//naturally aligned
	public BitVector(String str) {
		this(stringLength(str));
		//TODO can this be usefully optimized?
		for (int i = 0; i < finish; i++) {
			final char c = str.charAt(i);
			if (c == '1') setBit(finish - 1 - i, true);
			else if (c != '0') throw new IllegalArgumentException("Illegal character '" + c + "' at index " + i + ", expected '0' or '1'.");
		}
	}
	
	public BitVector(String str, int radix) {
		this(new BigInteger(str, radix));
	}
	
	public BitVector(Random random, int size) {
		this(size);
		for (int i = 0; i < bits.length; i++) {
			bits[i] = random.nextLong();
		}
	}
	
	private BitVector(int start, int finish, long[] bits, boolean mutable) {
		this.start = start;
		this.finish = finish;
		this.bits = bits;
		this.mutable = mutable;
	}
	
	private BitVector(Serial serial) {
		this(serial.start, serial.finish, serial.bits, serial.mutable);
	}
	
	//only called to support parsing with a different radix
	private BitVector(BigInteger bigInt) {
		this(bigInt.bitLength());
		//TODO ideally trap this earlier
		if (bigInt.signum() < 0) throw new IllegalArgumentException();
		for (int i = 0; i < finish; i++) {
			performSetAdj(i, bigInt.testBit(i));
		}
	}
	
	// accessors
	
	public int size() {
		return finish - start;
	}

	public boolean isAligned() {
		return start == 0;
	}
	
	public boolean isMutable() {
		return mutable;
	}
	
	// duplication

	//TODO consider adding a trimmed copy, or guarantee this is trimmed?
	//only creates a new bit vector if necessary
	public BitVector aligned() {
		return start == 0 ? this : getVectorAdj(start, finish - start, true);
	}

	public BitVector duplicate(boolean copy, boolean mutable) {
		if (mutable && !copy && !this.mutable) throw new IllegalStateException("Cannot obtain mutable view of an immutable BitVector");
		return new BitVector(start, finish, copy ? bits.clone() : bits, mutable);
	}
	
	public BitVector duplicate(int from, int to, boolean copy, boolean mutable) {
		if (mutable && !copy && !this.mutable) throw new IllegalStateException("Cannot obtain mutable view of an immutable BitVector");
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return new BitVector(from, to, copy ? bits.clone() : bits, mutable);
	}
	
	//only creates a new bit vector if necessary
	public BitVector mutable() {
		return mutable ? this : mutableCopy();
	}
	
	//only creates a new bit vector if necessary
	public BitVector immutable() {
		return mutable ? immutableCopy() : this;
	}
	
	public BitVector alignedCopy(boolean mutable) {
		return getVectorAdj(start, finish - start, mutable);
	}
	
	public BitVector resizedCopy(int newSize) {
		if (newSize < 0) throw new IllegalArgumentException();
		final int size = finish - start;
		if (newSize == size) return copy();
		if (newSize < size) return rangeCopy(0, newSize);
		final BitVector copy = new BitVector(newSize);
		copy.setVector(0, this);
		return copy;
	}
	
	// getters
	
	public boolean getBit(int position) {
		if (position < 0) throw new IllegalArgumentException();
		position += start;
		if (position >= finish) throw new IllegalArgumentException();
		//can't assume inlining, so duplicate getBitImpl here
		final int i = position >> ADDRESS_BITS;
		final long m = 1L << (position & ADDRESS_MASK);
		return (bits[i] & m) != 0;
	}
	
	public byte getByte(int position) {
		return (byte) getBits(position, 8);
	}

	public short getShort(int position) {
		return (byte) getBits(position, 16);
	}
	
	public int getInt(int position) {
		return (int) getBits(position, 32);
	}
	
	public long getLong(int position) {
		return (int) getBits(position, 64);
	}
	
	public long getBits(int position, int length) {
		if (position < 0) throw new IllegalArgumentException();
		if (length < 0) throw new IllegalArgumentException();
		position += start;
		if (position + length > finish) throw new IllegalArgumentException();
		if (length == 0) return 0L;
		final int i = position >> ADDRESS_BITS;
		final int s = position & ADDRESS_MASK;
		final long b;
		if (s == 0) { // fast case, long-aligned
			b = bits[i];
		} else if (s + length <= ADDRESS_SIZE) { //single long case
			b = bits[i] >>> s;
		} else {
			b = (bits[i] >>> s) | (bits[i+1] << (ADDRESS_SIZE - s));
		}
		return length == ADDRESS_SIZE ? b : b & ((1L << length) - 1);
	}
	
	//always mutable & aligned
	public BitVector getVector(int position, int length) {
		if (position < 0) throw new IllegalArgumentException();
		if (length < 0) throw new IllegalArgumentException();
		position += start;
		if (position + length > finish) throw new IllegalArgumentException();
		return getVectorAdj(position, length, true);
	}
	
	// bit counting methods
	
	public int countOnes() {
		return countOnesAdj(start, finish);
	}

	public int countOnes(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (from > to) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return countOnesAdj(from, to);
	}

	public int countZeros() {
		return finish - start - countOnes();
	}
	
	public int countZeros(int from, int to) {
		return to - from - countOnes(from, to);
	}

	// search methods

	public int firstOneInRange(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return firstOneInRangeAdj(from, to);
	}

	public int firstZeroInRange(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return firstZeroInRangeAdj(from, to);
	}

	public int lastOneInRange(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return lastOneInRangeAdj(from, to);
	}

	public int lastZeroInRange(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return lastZeroInRangeAdj(from, to);
	}

	// operations
	
	public void modify(Operation operation, boolean value) {
		performAdj(operation.ordinal(), start, finish, value);
	}

	public void modifyRange(Operation operation, int from, int to, boolean value) {
		perform(operation.ordinal(), from, to, value);
	}
	
	public void modifyBit(Operation operation, int position, boolean value) {
		perform(operation.ordinal(), position, value);
	}
	
	public void modifyBits(Operation operation, int position, long bits, int length) {
		perform(operation.ordinal(), position, bits, length);
	}
	
	public void modifyVector(Operation operation, BitVector vector) {
		perform(operation.ordinal(), vector);
	}
	
	public void modifyVector(Operation operation, int position, BitVector vector) {
		perform(operation.ordinal(), position, vector);
	}
	
	public void modifyBytes(Operation operation, int position, byte[] bytes, int offset, int length) {
		perform(operation.ordinal(), position, bytes, offset, length);
	}
	
	// rotations and shifts & reversals
	
	public void rotate(int distance) {
		rotateAdj(start, finish, distance);
	}

	public void rotateRange(int from, int to, int distance) {
		if (from < 0) throw new IllegalArgumentException();
		if (from > to) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		rotateAdj(from, to, distance);
	}
	
	public void shift(int distance, boolean fill) {
		shiftAdj(start, finish, distance, fill);
	}
	
	public void shiftRange(int from, int to, int distance, boolean fill) {
		if (from < 0) throw new IllegalArgumentException();
		if (from > to) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		shiftAdj(from, to, distance, fill);
	}
	
	public void reverse() {
		reverseAdj(start, finish);
	}
	
	public void reverseRange(int from, int to) {
		if (from < 0) throw new IllegalArgumentException();
		if (from > to) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		reverseAdj(from, to);
	}
	
	// comparisons
	
	public boolean compare(Comparison comparison, BitVector vector) {
		return compare(comparison.ordinal(), vector);
	}

	// tests
	
	public boolean isAll(boolean value) {
		return isAllAdj(start, finish, value);
	}
	
	public boolean isRangeAll(int from, int to, boolean value) {
		if (from < 0) throw new IllegalArgumentException();
		if (from > to) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		return isAllAdj(from, to, value);
	}
	
	// views
	
	public byte[] toByteArray() {
		//TODO can optimize when byte aligned
		final int size = finish - start;
		final int length = (size + 7) >> 3;
		final byte[] bytes = new byte[length];
		if (length == 0) return bytes;
		if ((start & ADDRESS_MASK) == 0) { //long aligned case
			int i = start >> ADDRESS_BITS;
			int j = length; //how many bytes we have left to process
			for (; j > 8; i++) {
				final long l = bits[i];
				bytes[--j] = (byte) (  l        & 0xff);
				bytes[--j] = (byte) ( (l >>  8) & 0xff);
				bytes[--j] = (byte) ( (l >> 16) & 0xff);
				bytes[--j] = (byte) ( (l >> 24) & 0xff);
				bytes[--j] = (byte) ( (l >> 32) & 0xff);
				bytes[--j] = (byte) ( (l >> 40) & 0xff);
				bytes[--j] = (byte) ( (l >> 48) & 0xff);
				bytes[--j] = (byte) ( (l >> 56) & 0xff);
			}
			if (j > 0) {
				final long m = -1L >>> (ADDRESS_SIZE - finish & ADDRESS_MASK);
				final long l = bits[i] & m;
				for (int k = 0; j > 0; k++) {
					bytes[--j] = (byte) ( (l >> (k*8)) & 0xff);
				}
			}
		} else { //general case
			//TODO indexing could probably be tidied up
			int i = 0;
			for (; i < length - 1; i++) {
				bytes[length - 1 - i] =  (byte) getBits(i << 3, 8);
			}
			bytes[0] = (byte) getBits(i * 8, size - (i << 3));
		}
		return bytes;
	}
	
	public BigInteger toBigInteger() {
		return start == finish ? BigInteger.ZERO : new BigInteger(1, toByteArray());
	}
	
	public BitSet toBitSet() {
		final int size = finish - start;
		final BitSet bitSet = new BitSet(size);
		for (int i = 0; i < size; i++) {
			bitSet.set(i, getBitAdj(i + start));
		}
		return bitSet;
	}
	
	// IO
	
	public void write(OutputStream out) throws IOException {
		//TODO could optimize for aligned instances
		final int size = finish - start;
		final int length = (size + 7) >> 3;
		if (length == 0) return;
		int p = size & 7;
		final int q = finish - p;
		if (p != 0) out.write((byte) getBitsAdj(q, p));
		p = q;
		while (p > start) {
			p -= 8;
			out.write((byte) getBitsAdj(p, 8));
		}
	}

	public void read(InputStream in) throws IOException {
		//TODO could optimize for aligned instances
		final int size = finish - start;
		final int length = (size + 7) >> 3;
		if (length == 0) return;
		int p = size & 7;
		final int q = finish - p;
		if (p != 0) performAdj(SET, q, (long) in.read(), p);
		p = q;
		while (p > start) {
			p -= 8;
			performAdj(SET, p, (long) in.read(), 8);
		}
	}
	
	// convenience setters
	
	//named flip for consistency with BigInteger and BitSet
	public void flip() {
		performAdj(XOR, start, finish, true);
	}
	
	public void flipBit(int position) {
		perform(XOR, position, true);
	}
	
	public void set(boolean value) {
		performAdj(SET, start, finish, value);
	}
	
	public void setRange(int from, int to, boolean value) {
		perform(SET, from, to, value);
	}
	
	public void setBit(int position, boolean value) {
		perform(SET, position, value);
	}

	public void setByte(int position, byte value) {
		perform(SET, position, value, 8);
	}
	
	public void setShort(int position, short value) {
		perform(SET, position, value, 16);
	}
	
	public void setInt(int position, short value) {
		perform(SET, position, value, 32);
	}
	
	public void setLong(int position, short value) {
		perform(SET, position, value, 64);
	}
	
	public void setBits(int position, long value, int length) {
		perform(SET, position, value, length);
	}
	
	public void setVector(BitVector vector) {
		perform(SET, vector);
	}

	public void setVector(int position, BitVector vector) {
		perform(SET, position, vector);
	}

	public void setBytes(int position, byte[] bytes, int offset, int length) {
		perform(SET, position, bytes, offset, length);
	}

	public void and(boolean value) {
		performAdj(AND, start, finish, value);
	}
	
	public void andRange(int from, int to, boolean value) {
		perform(AND, from, to, value);
	}
	
	public void andBit(int position, boolean value) {
		perform(AND, position, value);
	}
	
	public void andByte(int position, byte value) {
		perform(AND, position, value, 8);
	}
	
	public void andShort(int position, short value) {
		perform(AND, position, value, 16);
	}
	
	public void andInt(int position, short value) {
		perform(AND, position, value, 32);
	}
	
	public void andLong(int position, short value) {
		perform(AND, position, value, 64);
	}
	
	public void andBits(int position, long value, int length) {
		perform(AND, position, value, length);
	}
	
	public void andVector(BitVector vector) {
		perform(AND, vector);
	}
	
	public void andVector(int position, BitVector vector) {
		perform(AND, position, vector);
	}

	public void andBytes(int position, byte[] bytes, int offset, int length) {
		perform(AND, position, bytes, offset, length);
	}

	public void or(boolean value) {
		performAdj(OR, start, finish, value);
	}
	
	public void orRange(int from, int to, boolean value) {
		perform(OR, from, to, value);
	}
	
	public void orBit(int position, boolean value) {
		perform(OR, position, value);
	}

	public void orByte(int position, byte value) {
		perform(OR, position, value, 8);
	}
	
	public void orShort(int position, short value) {
		perform(OR, position, value, 16);
	}
	
	public void orInt(int position, short value) {
		perform(OR, position, value, 32);
	}
	
	public void orLong(int position, short value) {
		perform(OR, position, value, 64);
	}
	
	public void orBits(int position, long value, int length) {
		perform(OR, position, value, length);
	}
	
	public void orVector(BitVector vector) {
		perform(OR, vector);
	}
	
	public void orVector(int position, BitVector vector) {
		perform(OR, position, vector);
	}

	public void orBytes(int position, byte[] bytes, int offset, int length) {
		perform(OR, position, bytes, offset, length);
	}

	public void xor(boolean value) {
		performAdj(XOR, start, finish, value);
	}
	
	public void xorRange(int from, int to, boolean value) {
		perform(XOR, from, to, value);
	}
	
	public void xorBit(int position, boolean value) {
		perform(XOR, position, value);
	}

	public void xorByte(int position, byte value) {
		perform(XOR, position, value, 8);
	}
	
	public void xorShort(int position, short value) {
		perform(XOR, position, value, 16);
	}
	
	public void xorInt(int position, short value) {
		perform(XOR, position, value, 32);
	}
	
	public void xorLong(int position, short value) {
		perform(XOR, position, value, 64);
	}
	
	public void xorBits(int position, long value, int length) {
		perform(XOR, position, value, length);
	}
	
	public void xorVector(BitVector vector) {
		perform(XOR, vector);
	}
	
	public void xorVector(int position, BitVector vector) {
		perform(XOR, position, vector);
	}

	public void xorBytes(int position, byte[] bytes, int offset, int length) {
		perform(XOR, position, bytes, offset, length);
	}

	// convenience comparisons
	
	public boolean testEquals(BitVector vector) {
		return compare(EQUALS, vector);
	}
	
	public boolean testIntersects(BitVector vector) {
		return compare(INTERSECTS, vector);
	}
	
	public boolean testContains(BitVector vector) {
		return compare(CONTAINS, vector);
	}

	// convenience tests

	public boolean isAllZeros() {
		return isAllAdj(start, finish, false);
	}
	
	public boolean isRangeAllZeros(int from, int to) {
		return isRangeAll(from, to, false);
	}
	
	public boolean isAllOnes() {
		return isAllAdj(start, finish, true);
	}
	
	public boolean isRangeAllOnes(int from, int to) {
		return isRangeAll(from, to, true);
	}
	
	
	// convenience views
	
	//returns a new bitvector that is backed by the same data as this one
	//equivalent to clone
	public BitVector view() {
		return duplicate(false, mutable);
	}
	
	//returns a new bitvector that is backed by the same data as this one
	public BitVector rangeView(int from, int to) {
		return duplicate(from, to, false, mutable);
	}
	
	//returns a new mutable bitvector that is backed by the same data as this one
	public BitVector mutableView() {
		return duplicate(false, true);
	}
	
	//returns a new mutable bitvector that is backed by the same data as this one
	public BitVector mutableRangeView(int from, int to) {
		return duplicate(from, to, false, true);
	}
	
	//returns a new immutable bitvector that is backed by the same data as this one
	public BitVector immutableView() {
		return duplicate(false, false);
	}
	
	//returns a new immutable bitvector that is backed by the same data as this one
	public BitVector immutableRangeView(int from, int to) {
		return duplicate(from, to, false, false);
	}
	
	// convenience copies
	
	public BitVector copy() {
		return duplicate(true, mutable);
	}

	public BitVector rangeCopy(int from, int to) {
		return duplicate(from, to, true, mutable);
	}
	
	public BitVector immutableCopy() {
		return duplicate(true, false);
	}

	public BitVector immutableRangeCopy(int from, int to) {
		return duplicate(from, to, true, false);
	}
	
	public BitVector mutableCopy() {
		return duplicate(true, true);
	}

	public BitVector mutableRangeCopy(int from, int to) {
		return duplicate(from, to, true, true);
	}
	
	// convenience rotations and shifts
	// TODO consider removing these convenience methods
	
	public void rotateLeft(int distance) {
		rotate(distance);
	}
	
	public void rotateRight(int distance) {
		rotate(-distance);
	}
	
	public void shiftLeft(int distance) {
		shift(distance, false);
	}
	
	public void shiftRight(int distance) {
		shift(-distance, false);
	}

	// convenience searches

	public int firstBitInRange(int from, int to, boolean value) {
		return value ? firstOneInRange(from, to) : firstZeroInRange(from, to);
	}
	
	public int firstBit(boolean value) {
		return value ? firstOne() : firstZero();
	}

	public int firstOne() {
		return firstOneInRangeAdj(start, finish);
	}
	
	public int firstZero() {
		return firstZeroInRangeAdj(start, finish);
	}

	public int nextBit(int position, boolean value) {
		return value ? nextOne(position) : nextZero(position);
	}

	public int nextOne(int position) {
		if (position < 0) throw new IllegalArgumentException();
		position += start;
		if (position > finish) throw new IllegalArgumentException();
		return firstOneInRangeAdj(position, finish);
	}

	public int nextZero(int position) {
		if (position < 0) throw new IllegalArgumentException();
		position += start;
		if (position > finish) throw new IllegalArgumentException();
		return firstZeroInRangeAdj(position, finish);
	}

	public int lastBitInRange(int from, int to, boolean value) {
		return value ? lastOneInRange(from, to) : lastZeroInRange(from, to);
	}
	
	public int lastBit(boolean value) {
		return value ? lastOne() : lastZero();
	}

	public int lastOne() {
		return lastOneInRangeAdj(start, finish);
	}
	
	public int lastZero() {
		return lastZeroInRangeAdj(start, finish);
	}

	public int previousBit(int position, boolean value) {
		return value ? nextOne(position) : nextZero(position);
	}

	public int previousOne(int position) {
		if (position < 0) throw new IllegalArgumentException();
		position += start;
		if (position - 1 > finish) throw new IllegalArgumentException();
		return lastOneInRangeAdj(start, position);
	}

	public int previousZero(int position) {
		if (position < 0) throw new IllegalArgumentException();
		position += start;
		if (position - 1 > finish) throw new IllegalArgumentException();
		return lastZeroInRangeAdj(start, position);
	}

	// number methods
	
	@Override
	public byte byteValue() {
		return (byte) getBitsAdj(start, Math.min(8, finish-start));
	}
	
	@Override
	public short shortValue() {
		return (short) getBitsAdj(start, Math.min(16, finish-start));
	}
	
	@Override
	public int intValue() {
		return (int) getBitsAdj(start, Math.min(32, finish-start));
	}
	
	@Override
	public long longValue() {
		return (long) getBitsAdj(start, Math.min(64, finish-start));
	}
	
	@Override
	public float floatValue() {
		//TODO can make more efficient by writing a method that returns vector in base 10 string
		return toBigInteger().floatValue();
	}
	
	@Override
	public double doubleValue() {
		//TODO can make more efficient by writing a method that returns vector in base 10 string
		return toBigInteger().doubleValue();
	}
	
	// iterable methods
	
	@Override
	public Iterator<Boolean> iterator() {
		return new BitIterator();
	}

	public ListIterator<Boolean> listIterator() {
		return new BitIterator();
	}

	public ListIterator<Boolean> listIterator(int index) {
		if (index < 0) throw new IllegalArgumentException();
		index += start;
		if (index > finish) throw new IllegalArgumentException();
		return new BitIterator(index);
	}
	
	// object methods
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof BitVector)) return false;
		final BitVector that = (BitVector) obj;
		if (this.finish - this.start != that.finish - that.start) return false;
		return compare(EQUALS, that);
	}
	
	@Override
	public int hashCode() {
		final int size = finish - start;
		//trivial case
		if (size == 0) return size;
		int h = 0;
		//optimized case, starts at zero
		if (start == 0) {
			final int f = finish >> ADDRESS_BITS;
			for (int i = 0; i < f; i++) {
				final long l = bits[i];
				h = h * 31 + ((int) l       );
				h = h * 31 + ((int)(l >> 32));
			}
			if ((finish & ADDRESS_MASK) != 0) {
				final long m = -1L >>> (ADDRESS_SIZE - finish & ADDRESS_MASK);
				final long l = bits[f] & m;
				h = h * 31 + ((int) l       );
				h = h * 31 + ((int)(l >> 32));
			}
		} else {
			final int limit = size - ADDRESS_SIZE;
			for (int i = 0; i <= limit; i += ADDRESS_SIZE) {
				//TODO consider a getBitsImpl?
				long l = getBits(i, 64);
				h = h * 31 + ((int) l       );
				h = h * 31 + ((int)(l >> 32));
			}
			final int r = size & ADDRESS_MASK;
			if (r != 0) {
				final long l = getBits(size - r, r);
				h = h * 31 + ((int) l       );
				h = h * 31 + ((int)(l >> 32));
			}
		}
		return h ^ size;
	}
	
	@Override
	public String toString() {
		final int size = finish - start;
		switch (size) {
		case 0 : return "";
		case 1 : return getBitAdj(start) ? "1" : "0";
		default :
			StringBuilder sb = new StringBuilder(size);
			for (int i = finish - 1; i >= start; i--) {
				sb.append(getBitAdj(i) ? '1' : '0');
			}
			return sb.toString();
		}
	}
	
	public String toString(int radix) {
		return toBigInteger().toString(radix);
	}
	
	//shallow, externally identical to calling view();
	public BitVector clone() {
		try {
			return (BitVector) super.clone();
		} catch (CloneNotSupportedException e) {
			//should never occur
			throw new RuntimeException("Clone failure!", e);
		}
	}

	// serialization
	
	private Object writeReplace() throws ObjectStreamException {
		return new Serial(this);
	}
	
	// private utility methods

	private void perform(int operation, int position, boolean value) {
		if (position < 0)  throw new IllegalArgumentException();
		position += start;
		if (position >= finish) throw new IllegalArgumentException();
		performAdj(operation, position, value);
	}
	
	private void perform(int operation, int from, int to, boolean value) {
		if (from < 0) throw new IllegalArgumentException();
		if (to < from) throw new IllegalArgumentException();
		from += start;
		to += start;
		if (to > finish) throw new IllegalArgumentException();
		performAdj(operation, from, to, value);
	}

	private void performAdj(int operation, int from, int to, boolean value) {
		if (!mutable) throw new IllegalStateException();
		if (from == to) return; // nothing to do for an empty vector
		
		//rationalize possible operations into SETs or INVERTs
		switch (operation) {
		case AND: if (value == false) performAdj(SET, from, to, false); else return; 
		case OR:  if (value == true) performAdj(SET, from, to, true); else return;
		case XOR : if (value == false) return;
		}
		
		final int f = from >> ADDRESS_BITS;
		final int t = (to-1) >> ADDRESS_BITS;
		final long fm = -1L << (from - f * ADDRESS_SIZE);
		final long tm = -1L >>> (t * ADDRESS_SIZE - to);
		
		if (f == t) { // change falls into one element
			final long mask = fm & tm;
			switch (operation) {
			case SET :
				if (value) {
					bits[f] |= mask;
				} else {
					bits[f] &= ~mask;
				}
				break;
			case XOR :
				bits[f] ^= mask;
				break;
			}
			return;
		}
		
		switch (operation) { //process intermediate elements
		case SET :
			Arrays.fill(bits, f+1, t, value ? -1L : 0L);
			break;
		case XOR :
			for (int i = f+1; i < t; i++) bits[i] = ~bits[i];
			break;
		}
		
		//process terminals
		switch (operation) {
		case SET :
			if (value) {
				bits[f] |= fm;
				bits[t] |= tm;
			} else {
				bits[f] &= ~fm;
				bits[t] &= ~tm;
			}
			break;
		case XOR :
			bits[f] ^= fm;
			bits[t] ^= tm;
			break;
		}
	}

	//assumes address size is size of long
	private void perform(int operation, int position, long bs, int length) {
		if (position < 0) throw new IllegalArgumentException();
		if (length < 0 || length > ADDRESS_SIZE) throw new IllegalArgumentException();
		position += start;
		if (position + length > finish) throw new IllegalArgumentException();
		if (!mutable) throw new IllegalStateException();
		performAdj(operation, position, bs, length);
	}

	private void performAdj(int operation, int position, long bs, int length) {
		if (length == 0) return;
		final int i = position >> ADDRESS_BITS;
		final int s = position & ADDRESS_MASK;
		final long m = length == ADDRESS_SIZE ? -1L : (1L << length) - 1L;
		final long v = bs & m;
		if (s == 0) { // fast case, long-aligned
			switch (operation) {
			case SET : bits[i] = bits[i] & ~m | v; break;
			case AND : bits[i] &= v | ~m; break;
			case OR  : bits[i] |= v; break;
			case XOR : bits[i] ^= v; break;
			}
		} else if (s + length <= ADDRESS_SIZE) { //single long case
			switch (operation) {
			case SET : bits[i] = bits[i] & Long.rotateLeft(~m, s) | (v << s); break;
			case AND : bits[i] &= (v << s) | Long.rotateLeft(~m, s); break;
			case OR  : bits[i] |= v << s; break;
			case XOR : bits[i] ^= v << s; break;
			}
			
		} else {
			switch (operation) {
			case SET :
				bits[i  ] = bits[i  ] & (-1L >>> (         ADDRESS_SIZE - s)) | (v <<                  s );
				bits[i+1] = bits[i+1] & (-1L <<  (length - ADDRESS_SIZE + s)) | (v >>> (ADDRESS_SIZE - s));
				break;
			case AND :
				bits[i  ]  &= (v <<                  s ) | (-1L >>> (         ADDRESS_SIZE - s));
				bits[i+1]  &= (v >>> (ADDRESS_SIZE - s)) | (-1L <<  (length - ADDRESS_SIZE + s));
				break;
			case OR  :
				bits[i  ]  |= (v <<                  s );
				bits[i+1]  |= (v >>> (ADDRESS_SIZE - s));
				break;
			case XOR :
				bits[i  ]  ^= (v <<                  s );
				bits[i+1]  ^= (v >>> (ADDRESS_SIZE - s));
				break;
			}
		}
	}

	private void perform(int operation, BitVector that) {
		if (this.size() != that.size()) throw new IllegalArgumentException("mismatched vector size");
		perform(operation, 0, that);
	}

	private void perform(int operation, int position, BitVector that) {
		if (that == null) throw new IllegalArgumentException("null vector");
		final int thatSize = that.size();
		if (this.bits == that.bits && overlapping(position, position + thatSize, that.start, that.finish)) that = that.copy();
		if (thatSize == 0) return;
		if (thatSize <= ADDRESS_SIZE) {
			perform(operation, position, that.getBits(0, thatSize), thatSize);
			return;
		}
		if (position < 0) throw new IllegalArgumentException("negative position");
		position += this.start;
		if (position + that.finish - that.start > this.finish) throw new IllegalArgumentException();
		//TODO *really* need to optimize this
		for (int s = that.start; s < that.finish; s++) {
			performAdj(operation, position++, that.getBitAdj(s));
		}
	}
	
	private void perform(int operation, int position, byte[] bytes, int offset, int length) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		if (position < 0) throw new IllegalArgumentException("negative position");
		if (offset < 0) throw new IllegalArgumentException("negative offset");
		if (length == 0) return;
		if (offset + length > (bytes.length << 3)) throw new IllegalArgumentException("length greater than number of bits in byte array");
		position += start;
		if (position + length > finish) throw new IllegalArgumentException("operation exceeds length of bit vector");
		performAdj(operation, position, bytes, offset, length);
	}
	
	private boolean compare(final int comp, final BitVector that) {
		if (this.finish - this.start != that.finish - that.start) throw new IllegalArgumentException();
		//trivial case
		if (this.start == this.finish) {
			switch (comp) {
			case EQUALS : return true;
			case INTERSECTS : return false;
			case CONTAINS : return true;
			default : throw new IllegalArgumentException("Unexpected comparison constant: " + comp);
			}
		}
		//TODO worth optimizing for case where this == that?
		//fully optimal case - both start at 0
		//TODO can weaken this constraint - can optimize if their start are equal
		if (this.start == 0 && that.start == 0) {
			final long[] thisBits = this.bits;
			final long[] thatBits = that.bits;
			final int t = (finish-1) >> ADDRESS_BITS;
			switch (comp) {
			case EQUALS :
				for (int i = t-1; i >= 0; i--) {
					if (thisBits[i] != thatBits[i]) return false;
				}
				break;
			case INTERSECTS :
				for (int i = t-1; i >= 0; i--) {
					if ((thisBits[i] & thatBits[i]) != 0) return true;
				}
				break;
			case CONTAINS :
				for (int i = t-1; i >= 0; i--) {
					final long bits = thisBits[i];
					if ((bits | thatBits[i]) != bits) return false;
				}
				break;
			default : throw new IllegalArgumentException("Unexpected comparison constant: " + comp); 
			}
			{
				// same length & same start so same finish mask
				final long m = -1L >>> (ADDRESS_SIZE - finish & ADDRESS_MASK);
				final long thisB = thisBits[t] & m;
				final long thatB = thatBits[t] & m;
				switch (comp) {
				case EQUALS : return thisB == thatB;
				case INTERSECTS : return (thisB & thatB) != 0;
				case CONTAINS : return (thisB | thatB) == thisB;
				default : throw new IllegalArgumentException("Unexpected comparison constant: " + comp);
				}
			}
		}
		//TODO an additional optimization is possible when their starts differ by 64 
		//partially optimal case - both are address aligned
		if ((this.start & ADDRESS_MASK) == 0 && (that.start & ADDRESS_MASK) == 0 && (this.finish & ADDRESS_MASK) == 0) {
			final long[] thisBits = this.bits;
			final long[] thatBits = that.bits;
			final int f = this.start >> ADDRESS_BITS;
			final int t = this.finish >> ADDRESS_BITS;
			final int d = (that.start - this.start) >> ADDRESS_BITS;
			switch (comp) {
			case EQUALS :
				for (int i = f; i < t; i++) {
					if (thisBits[i] != thatBits[i+d]) return false;
				}
				return true;
			case INTERSECTS :
				for (int i = f; i < t; i++) {
					if ((thisBits[i] & thatBits[i+d]) != 0) return true;
				}
				return false;
			case CONTAINS :
				for (int i = f; i < t; i++) {
					final long bits = thisBits[i];
					if ((bits | thatBits[i+d]) != bits) return false;
				}
				return true;
			default : throw new IllegalArgumentException("Unexpected comparison constant: " + comp);
			}
		}
		//non-optimized case
		//TODO consider if this can be gainfully optimized
		final int size = finish - start;
		switch (comp) {
		case EQUALS :
			for (int i = 0; i < size; i++) {
				if (that.getBitAdj(that.start + i) != this.getBitAdj(this.start + i)) return false;
			}
			return true;
		case INTERSECTS :
			for (int i = 0; i < size; i++) {
				if (that.getBitAdj(that.start + i) && this.getBitAdj(this.start + i)) return true;
			}
			return false;
		case CONTAINS :
			for (int i = 0; i < size; i++) {
				if (that.getBitAdj(that.start + i) && !this.getBitAdj(this.start + i)) return false;
			}
			return true;
		default : throw new IllegalArgumentException("Unexpected comparison constant: " + comp);
		}
	}

	private boolean getBitAdj(int position) {
		final int i = position >> ADDRESS_BITS;
		final long m = 1L << (position & ADDRESS_MASK);
		return (bits[i] & m) != 0;
	}

	private long getBitsAdj(int position, int length) {
		final int i = position >> ADDRESS_BITS;
		if (i >= bits.length) return 0L; // may happen if position == finish
		final int s = position & ADDRESS_MASK;
		final long b;
		if (s == 0) { // fast case, long-aligned
			b = bits[i];
		} else if (s + length <= ADDRESS_SIZE) { //single long case
			b = bits[i] >>> s;
		} else {
			b = (bits[i] >>> s) | (bits[i+1] << (ADDRESS_SIZE - s));
		}
		return length == ADDRESS_SIZE ? b : b & ((1L << length) - 1);
	}
	private BitVector getVectorAdj(int position, int length, boolean mutable) {
		final long[] newBits;
		if (length == finish) {
			newBits = bits.clone();
		} else if (length == 0) {
			newBits = new long[0];
		} else {
			final int from = position >> ADDRESS_BITS;
			final int to = (position + length + ADDRESS_MASK) >> ADDRESS_BITS;
			if ((position & ADDRESS_MASK) == 0) {
				newBits = copier.copy(bits, from, to);
			} else {
				final int s = position & ADDRESS_MASK;
				final int len = to - from;
				newBits = new long[len];
				//do all but last bit
				int j = from;
				int i = 0;
				for (; i < len - 1; i++, j++) {
					newBits[i] = (bits[j] >>> s) | (bits[j+1] << (ADDRESS_SIZE - s));
				}
				//do last bits as a special case
				if (j+1 < len) {
					newBits[i] = (bits[j] >>> s) | (bits[j+1] << (ADDRESS_SIZE - s));
				} else {
					newBits[i] = bits[j] >>> s;
				}
			}
		}
		return new BitVector(0, length, newBits, mutable);
	}

	private int countOnesAdj(int from, int to) {
		if (from == to) return 0;
		final int f = from >> ADDRESS_BITS;
		final int t = (to-1) >> ADDRESS_BITS;
		final int r = from & ADDRESS_MASK;
		final int l = to & ADDRESS_MASK;
		if (f == t) {
			//alternatively: (0x8000000000000000L >> (l - r - 1)) >>> (ADDRESS_SIZE - l);
			final long m = (-1L >>> (ADDRESS_SIZE - l + r)) << r;
			return Long.bitCount(m & bits[f]);  
		}

		int count = 0;
		count += Long.bitCount( (-1L << r) & bits[f] );
		for (int i = f+1; i < t; i++) {
			count += Long.bitCount(bits[i]);
		}
		count += Long.bitCount( (-1L >>> (ADDRESS_SIZE - l)) & bits[t] );
		return count;
	}

	private boolean isAllAdj(int from, int to, boolean value) {
		if (from == to) return true;
		final int f = from >> ADDRESS_BITS;
		final int t = (to-1) >> ADDRESS_BITS;
		
		final long fm;
		final long tm;
		{
			final int fs = from & ADDRESS_MASK;
			final int ts = to & ADDRESS_MASK;
			if (value) {
				fm = fs == 0 ? 0 : -1L >>> (ADDRESS_SIZE - fs);
				tm = ts == 0 ? 0 : -1L << ts;
			} else {
				fm = -1L << fs;
				tm = -1L >>> (ADDRESS_SIZE - ts);
			}
		}

		if (f == t) { // bits fit into a single element
			if (value) {
				return (bits[f] | fm | tm) == -1L;
			} else {
				return (bits[f] & fm & tm) == 0L;
			}
		}

		//check intermediate elements
		if (value) {
			for (int i = f+1; i < t; i++) if (bits[i] != -1L) return false;
		} else {
			for (int i = f+1; i < t; i++) if (bits[i] != 0L) return false;
		}

		//check terminals
		if (value) {
			return (bits[f] | fm) == -1L && (bits[t] | tm) == -1L;
		} else {
			return (bits[f] & fm) == 0L && (bits[t] & tm) == 0L;
		}
	}

	private void performAdj(int operation, int position, boolean value) {
		if (!mutable) throw new IllegalStateException();
		final int i = position >> ADDRESS_BITS;
		final long m = 1L << (position & ADDRESS_MASK);
		switch(operation) {
		case SET : 
			if (value) {
				bits[i] |=  m;
			} else {
				bits[i] &= ~m;
			}
			break;
		case AND :
			if (value) {
				/* no-op */
			} else {
				bits[i] &= ~m;
			}
			break;
		case OR :
			if (value) {
				bits[i] |=  m;
			} else {
				/* no-op */
			}
			break;
		case XOR :
			if (value) {
				bits[i] ^=  m;
			} else {
				/* no-op */
			}
			break;
		}

	}
	
	//TODO really needs a more efficient implementation (see below for a failure)
	private void performAdj(int operation, int position, byte[] bytes, int offset, int length) {
		if (!mutable) throw new IllegalStateException();
		final int to = (bytes.length << 3) - offset;
		final int from = to - length;
		position += length;
		for (int i = from; i < to; i++) {
			boolean b = (bytes[i >> 3] & (128 >> (i & 7))) != 0;
			performAdj(operation, --position, b);
		}
	}
/*
	// implementation that is totally bogus because array needs to considered bwards
	private void performAdj(int operation, int position, byte[] bytes, int offset, int length) {
		if (!mutable) throw new IllegalStateException();
		final int limit = offset + length;
		//knock off any initial unaligned bits
		if ((offset & 7) != 0) {
			int prelim = Math.min( (offset | 7) + 1, limit);
			for (; offset < prelim; offset++) {
				boolean b = ((bytes[offset >> 3] >> (offset & 7) ) & 1) != 0;
				performAdj(operation, position++, b);
			}
			length = limit - offset;
			if (length == 0) return;
		}
		//at this point we are byte aligned
		final int byteLimit = limit >> 3;
		int j = offset >> 3;
		//bunch as many bytes as we can into longs and operate with those
		for (; j + 8 <= byteLimit; j += 8) {
			final long bs =
				((bytes[j    ] & 0xff) << 56) |
				((bytes[j + 1] & 0xff) << 48) |
				((bytes[j + 2] & 0xff) << 40) |
				((bytes[j + 3] & 0xff) << 32) |
				((bytes[j + 4] & 0xff) << 24) |
				((bytes[j + 5] & 0xff) << 16) |
				((bytes[j + 6] & 0xff) <<  8) |
				((bytes[j + 7] & 0xff)      );
			performAdj(operation, position, bs, 64);
			position += 64;
		}
		//now we have less than a long's worth of bits left, operate in bytes
		for (; j < byteLimit; j++) {
			performAdj(operation, position, bytes[j], 8);
			position += 8;
		}
		//finally we may have less than a byte's worth of bits left - mop them up
		offset = j << 3;
		length = limit - offset;
		if (length > 0) performAdj(operation, position, bytes[j], length);
	}
*/
	//specialized implementation for the common case of setting an individual bit
	
	private void performSetAdj(int position, boolean value) {
		if (!mutable) throw new IllegalStateException();
		final int i = position >> ADDRESS_BITS;
		final long m = 1L << (position & ADDRESS_MASK);
		if (value) {
			bits[i] |=  m;
		} else {
			bits[i] &= ~m;
		}
	}

	//separate implementation from performAdj is an optimization
	
	private boolean getAndPerformAdj(int operation, int position, boolean value) {
		if (!mutable) throw new IllegalStateException();
		final int i = position >> ADDRESS_BITS;
		final long m = 1L << (position & ADDRESS_MASK);
		final long v = bits[i] & m;
		switch(operation) {
		case SET : 
			if (value) {
				bits[i] |=  m;
			} else {
				bits[i] &= ~m;
			}
			break;
		case AND :
			if (value) {
				/* no-op */
			} else {
				bits[i] &= ~m;
			}
			break;
		case OR :
			if (value) {
				bits[i] |=  m;
			} else {
				/* no-op */
			}
			break;
		case XOR :
			if (value) {
				bits[i] ^=  m;
			} else {
				/* no-op */
			}
			break;
		}
		return v != 0;
	}
	
	public void rotateAdj(int from, int to, int distance) {
		final int length = to - from;
		if (length < 2) return;
		distance = distance % length;
		if (distance < 0) distance += length;
		if (distance == 0) return;
		
		//TODO is this capable of optimization in some cases?
		final int cycles = gcd(distance, length);
		for (int i = from + cycles - 1; i >= from; i--) {
			boolean m = getBitAdj(i); // the previously overwritten value
			int j = i; // the index that is to be overwritten next
			do {
				j += distance;
				if (j >= to) j -= length;
				m = getAndPerformAdj(SET, j, m);
			} while (j != i);
		}
	}

	public void shiftAdj(int from, int to, int distance, boolean fill) {
		if (from == to) return;
		if (distance == 0) return;
		
		//TODO this capable of optimization in some cases
		if (distance > 0) {
			int j = to - 1;
			for (int i = j - distance; i >= from; i--, j--) {
				performSetAdj(j, getBitAdj(i));
			}
			performAdj(SET, from, j + 1, fill);
		} else {
			int j = from;
			for (int i = j - distance; i < to; i++, j++) {
				performSetAdj(j, getBitAdj(i));
			}
			performAdj(SET, j, to, fill);
		}
		
	}

	private void reverseAdj(int from, int to) {
		to--;
		while (from < to) {
			performSetAdj(to, getAndPerformAdj(SET, from, getBitAdj(to)));
			from++; to--;
		}
	}

	//TODO can eliminate calls to getBitsAdj from these methods
	
	private int firstOneInRangeAdj(int from, int to) {
		// trivial case
		if (from == to) return -1;
		final int size = to - from;
		//simple case
		if (size <= ADDRESS_SIZE) {
			final int j = Long.numberOfTrailingZeros( getBitsAdj(from, size) );
			return j >= size ? -1 : from + j - start;
		}
		int i = from;
		// check head
		int a = i & ADDRESS_MASK;
		if (a != 0) {
			final int s = ADDRESS_SIZE - a;
			final int j = Long.numberOfTrailingZeros( getBitsAdj(i, s) );
			if (j < s) return from + j - start;
			i += s;
		}
		// check body
		final int b = to & ADDRESS_MASK;
		final int t = to - b;
		while (i < t) {
			final int j = Long.numberOfTrailingZeros( bits[i >> ADDRESS_BITS] );
			if (j < ADDRESS_SIZE) return i + j - start;
			i += ADDRESS_SIZE;
		}
		// check tail
		if (b != 0) {
			final int j = Long.numberOfTrailingZeros( getBitsAdj(t, b) );
			return j >= b ? -1 : i + j - start;
		}
		// give up
		return -1;
	}

	private int firstZeroInRangeAdj(int from, int to) {
		// trivial case
		if (from == to) return -1;
		final int size = to - from;
		//simple case
		if (size <= ADDRESS_SIZE) {
			final int j = Long.numberOfTrailingZeros( ~getBitsAdj(from, size) );
			return j >= size ? -1 : from + j - start;
		}
		int i = from;
		// check head
		int a = i & ADDRESS_MASK;
		if (a != 0) {
			final int s = ADDRESS_SIZE - a;
			final int j = Long.numberOfTrailingZeros( ~getBitsAdj(i, s) );
			if (j < s) return from + j - start;
			i += s;
		}
		// check body
		final int b = to & ADDRESS_MASK;
		final int t = to - b;
		while (i < t) {
			final int j = Long.numberOfTrailingZeros( ~bits[i >> ADDRESS_BITS] );
			if (j < ADDRESS_SIZE) return i + j - start;
			i += ADDRESS_SIZE;
		}
		// check tail
		if (b != 0) {
			final int j = Long.numberOfTrailingZeros( ~getBitsAdj(t, b) );
			return j >= b ? -1 : i + j - start;
		}
		// give up
		return -1;
	}

	private int lastOneInRangeAdj(int from, int to) {
		// trivial case
		if (from == to) return -1;
		final int size = to - from;
		//simple case
		if (size <= ADDRESS_SIZE) {
			final int j = Long.numberOfLeadingZeros( getBitsAdj(from, size) << (ADDRESS_SIZE - size) );
			return j == ADDRESS_SIZE ? -1 : to - (j + 1) - start;
		}
		// check tail
		final int b = to & ADDRESS_MASK;
		int i = to - b;
		if (b != 0) {
			final int j = Long.numberOfLeadingZeros( getBitsAdj(i, b) << (ADDRESS_SIZE - b) );
			if (j != ADDRESS_SIZE) return to - (j + 1) - start;
		}
		// check body
		final int a = from & ADDRESS_MASK;
		final int f = from + (ADDRESS_SIZE - a);
		while (i >= f) {
			i -= ADDRESS_SIZE;
			final int j = Long.numberOfLeadingZeros( bits[i >> ADDRESS_BITS] );
			if (j != ADDRESS_SIZE) return i + ADDRESS_SIZE - (j + 1) - start;
		}
		// check head
		if (a != 0) {
			final int s = ADDRESS_SIZE - a;
			final int j = Long.numberOfLeadingZeros( getBitsAdj(from, s) << a );
			if (j != ADDRESS_SIZE) return i - (j + 1) - start;
		}
		// give up
		return -1;
	}
	
	private int lastZeroInRangeAdj(int from, int to) {
		// trivial case
		if (from == to) return -1;
		final int size = to - from;
		//simple case
		if (size <= ADDRESS_SIZE) {
			final int j = Long.numberOfLeadingZeros( ~getBitsAdj(from, size) << (ADDRESS_SIZE - size) );
			return j == ADDRESS_SIZE ? -1 : to - (j + 1) - start;
		}
		// check tail
		final int b = to & ADDRESS_MASK;
		int i = to - b;
		if (b != 0) {
			final int j = Long.numberOfLeadingZeros( ~getBitsAdj(i, b) << (ADDRESS_SIZE - b) );
			if (j != ADDRESS_SIZE) return to - (j + 1) - start;
		}
		// check body
		final int a = from & ADDRESS_MASK;
		final int f = from + (ADDRESS_SIZE - a);
		while (i >= f) {
			i -= ADDRESS_SIZE;
			final int j = Long.numberOfLeadingZeros( ~bits[i >> ADDRESS_BITS] );
			if (j != ADDRESS_SIZE) return i + ADDRESS_SIZE - (j + 1) - start;
		}
		// check head
		if (a != 0) {
			final int s = ADDRESS_SIZE - a;
			final int j = Long.numberOfLeadingZeros( ~getBitsAdj(from, s) << a );
			if (j != ADDRESS_SIZE) return i - (j + 1) - start;
		}
		// give up
		return -1;
	}
	
	// inner classes
	
	private class BitIterator implements ListIterator<Boolean> {
		
		private final int from;
		private final int to;
		// points to the element that will be returned  by next
		private int index;

		BitIterator(int from, int to, int index) {
			this.from = from;
			this.to = to;
			this.index = index;
		}
		
		BitIterator(int index) {
			this(start, finish, index);
		}
		
		BitIterator() {
			this(start, finish, start);
		}
		
		@Override
		public boolean hasNext() {
			return index < to;
		}
		
		@Override
		public Boolean next() {
			if (!hasNext()) throw new NoSuchElementException();
			return Boolean.valueOf( getBitAdj(index++) );
		}
		
		@Override
		public int nextIndex() {
			return hasNext() ? index - start : -1;
		}
		
		@Override
		public boolean hasPrevious() {
			return index > from;
		}
		
		@Override
		public Boolean previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return Boolean.valueOf( getBitAdj(--index) );
		}

		@Override
		public int previousIndex() {
			return hasPrevious() ? index - start - 1 : -1;
		}
		
		@Override
		public void set(Boolean bit) {
			setBit(index, bit);
		}
		
		@Override
		public void add(Boolean bit) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static class Serial implements Serializable {
		
		private static final long serialVersionUID = -1476938830216828886L;

		private int start;
		private int finish;
		private long[] bits;
		private boolean mutable;
		
		Serial(BitVector v) {
			start = v.start;
			finish = v.finish;
			bits = v.bits;
			mutable = v.mutable;
		}
		
		private Object readResolve() throws ObjectStreamException {
			return new BitVector(this);
		}
		
	}

	// classes to accomodate environments that don't have Arrays.copyOfRange
	
	private interface ArrayCopier {
		
		long[] copy(long[] array, int from, int to);
	}
	
	private static class RangeCopier implements ArrayCopier {

		@Override
		public long[] copy(long[] array, int from, int to) {
			return Arrays.copyOfRange(array, from, to);
		}
		
	}

	private static class SystemCopier implements ArrayCopier {
		
		@Override
		public long[] copy(long[] array, int from, int to) {
			long[] copy = new long[to - from];
			System.arraycopy(array, from, copy, 0, to - from);
			return copy;
		}
		
	}
	
}
