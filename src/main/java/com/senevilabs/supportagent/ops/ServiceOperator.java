package com.senevilabs.supportagent.ops;

public interface ServiceOperator {
    String check(String service);
    String restart(String service);
}
