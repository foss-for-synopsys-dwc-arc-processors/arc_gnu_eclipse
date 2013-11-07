package com.arc.embeddedcdt.gui.jtag;

import java.io.File;

public interface ITransform
{

	void transform(String strip, File from, File to);

}
