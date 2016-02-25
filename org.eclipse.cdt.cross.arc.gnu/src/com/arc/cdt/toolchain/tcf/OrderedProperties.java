/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.cdt.toolchain.tcf;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class OrderedProperties extends Properties {
    private static final long serialVersionUID = -7877028921359969019L;
    private Vector<String> _names;

    public OrderedProperties() {
        super();
        _names = new Vector<>();
    }

    @Override
    public Enumeration<String> propertyNames() {
        return _names.elements();
    }

    @Override
    public Object put(Object key, Object value) {
        if (_names.contains(key)) {
            _names.remove(key);
        }
        _names.add(key.toString());
        return super.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        _names.remove(key);
        return super.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append('{');
        for (String name: _names) {
            builder.append(name).append('=').append(this.get(name)).append(',');
        }
        builder.setCharAt(builder.length() - 1, '}');
        return builder.toString();
    }
}
