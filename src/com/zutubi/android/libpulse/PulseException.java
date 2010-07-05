package com.zutubi.android.libpulse;

/**
 * Runtime exception for errors talking to a Pulse server.
 */
public class PulseException extends RuntimeException
{
    private static final long serialVersionUID = -5093036569719326446L;

    public PulseException()
    {
        super();
    }

    public PulseException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public PulseException(String detailMessage)
    {
        super(detailMessage);
    }

    public PulseException(Throwable throwable)
    {
        super(throwable);
    }
}
