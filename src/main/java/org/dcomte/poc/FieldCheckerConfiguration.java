package org.dcomte.poc;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
class FieldCheckerConfiguration extends DynamicBooleanAccessorsImpl {
    Boolean field1 = false;
    Boolean field2 = false;
    Boolean field3 = false;
    Boolean field4 = false;
    Boolean field5 = false;
    Boolean field6 = false;
    Boolean field7 = false;
    Boolean lastField = false;
}
