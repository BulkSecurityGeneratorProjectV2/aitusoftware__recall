package com.aitusoftware.recall.example;

import com.aitusoftware.recall.persistence.AsciiCharSequence;
import com.aitusoftware.recall.persistence.Decoder;
import com.aitusoftware.recall.persistence.Encoder;
import org.agrona.concurrent.UnsafeBuffer;

public class OrderUnsafeBufferTranscoder implements Encoder<UnsafeBuffer, Order>, Decoder<UnsafeBuffer, Order>
{
    @Override
    public void load(final UnsafeBuffer buffer, final int offset, final Order container)
    {
        container.setId(buffer.getLong(offset + ID_OFFSET));
        container.setInstrumentId(buffer.getLong(offset + INSTRUMENT_ID_OFFSET));
        container.setCreatedEpochSeconds(buffer.getLong(offset + CREATED_SECONDS_OFFSET));
        container.setCreatedNanos(buffer.getInt(offset + CREATED_NANOS_OFFSET));
        container.setExecutedAtEpochSeconds(buffer.getLong(offset + EXECUTED_SECONDS_OFFSET));
        container.setExecutedAtNanos(buffer.getInt(offset + EXECUTED_NANOS_OFFSET));
        int charCount = buffer.getInt(offset + SYMBOL_LENGTH_OFFSET);
        AsciiCharSequence symbolCharSequence = container.getSymbolCharSequence();
        symbolCharSequence.reset();
        for (int i = 0; i < charCount; i++)
        {
            symbolCharSequence.append((char) buffer.getByte(offset + SYMBOL_VALUE_OFFSET + i));
        }
    }

    @Override
    public void store(final UnsafeBuffer buffer, final int offset, final Order value)
    {
        buffer.putLong(offset + ID_OFFSET, value.getId());
        buffer.putLong(offset + INSTRUMENT_ID_OFFSET, value.getInstrumentId());
        buffer.putLong(offset + CREATED_SECONDS_OFFSET, value.getCreatedEpochSeconds());
        buffer.putInt(offset + CREATED_NANOS_OFFSET, value.getCreatedNanos());
        buffer.putLong(offset + EXECUTED_SECONDS_OFFSET, value.getExecutedAtEpochSeconds());
        buffer.putInt(offset + EXECUTED_NANOS_OFFSET, value.getExecutedAtNanos());
        final CharSequence symbol = value.getSymbol();
        buffer.putInt(offset + SYMBOL_LENGTH_OFFSET, symbol.length());
        for (int i = 0; i < symbol.length(); i++) {
            buffer.putByte(offset + SYMBOL_VALUE_OFFSET + i, (byte) symbol.charAt(i));
        }
    }

    private static final int ID_OFFSET = 0;
    private static final int INSTRUMENT_ID_OFFSET = ID_OFFSET + Long.BYTES;
    private static final int CREATED_SECONDS_OFFSET = INSTRUMENT_ID_OFFSET + Long.BYTES;
    private static final int CREATED_NANOS_OFFSET = CREATED_SECONDS_OFFSET + Long.BYTES;
    private static final int EXECUTED_SECONDS_OFFSET = CREATED_NANOS_OFFSET + Integer.BYTES;
    private static final int EXECUTED_NANOS_OFFSET = EXECUTED_SECONDS_OFFSET + Long.BYTES;
    private static final int SYMBOL_LENGTH_OFFSET = EXECUTED_NANOS_OFFSET + Integer.BYTES;
    private static final int SYMBOL_VALUE_OFFSET = SYMBOL_LENGTH_OFFSET + Integer.BYTES;
}
