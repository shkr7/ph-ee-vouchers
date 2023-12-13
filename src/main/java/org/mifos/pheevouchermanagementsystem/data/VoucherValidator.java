package org.mifos.pheevouchermanagementsystem.data;

import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.pheevouchermanagementsystem.util.VoucherValidatorsEnum;
import org.springframework.stereotype.Component;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;

@Component
public class VoucherValidator {
    private static final String resource = "voucherValidator";
    private static final String requestId = "requestId";
    private static final int expectedRequestIdLength = 12;
    private static final String batchId = "batchId";
    private static final int expectedBatchIdLength = 12;
    private static final String voucherInstructions = "voucherInstructions";
    private static final String instructionID = "instructionID";
    private static final int expectedInstructionIdLength = 16;
    private static final String groupCode = "groupCode";
    private static final int expectedGroupCodeLength = 3;
    private static final String currency = "currency";
    private static final int expectedCurrencyLength = 3;
    private static final String amount = "amount";
    private static final String payeeFunctionalID = "payeeFunctionalID";
    private static final int expectedPayeeFunctionalIDLength = 20;
    private static final String narration = "narration";
    private static final int  maximumNarrationLength = 50;

    public PhErrorDTO validateCreateVoucher(RequestDTO request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        //Checks for requestID
        validatorBuilder.reset().resource(resource).parameter(requestId).value(request.getRequestID())
                .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_REQUEST_ID)
                .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedRequestIdLength, VoucherValidatorsEnum.INVALID_REQUEST_ID_LENGTH);

        //Checks for batchID
        validatorBuilder.reset().resource(resource).parameter(batchId).value(request.getBatchID())
                .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_BATCH_ID)
                .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedBatchIdLength, VoucherValidatorsEnum.INVALID_BATCH_ID_LENGTH);

        //Check for voucherInstructions
        validatorBuilder.reset().resource(resource).parameter(voucherInstructions).value(request.getVoucherInstructions())
                .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_VOUCHER_INSTRUCTIONS);

        request.getVoucherInstructions().forEach(voucherInstruction -> {
            // Check for instructionID
            validatorBuilder.reset().resource(resource).parameter(instructionID).value(voucherInstruction.getInstructionID())
                    .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_INSTRUCTION_ID)
                    .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedInstructionIdLength, VoucherValidatorsEnum.INVALID_INSTRUCTION_ID_LENGTH);

            // Check for groupCode
            validatorBuilder.reset().resource(resource).parameter(groupCode).value(voucherInstruction.getGroupCode())
                    .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_GROUP_CODE)
                    .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedGroupCodeLength, VoucherValidatorsEnum.INVALID_GROUP_CODE_LENGTH);

            // Check for currency
            validatorBuilder.reset().resource(resource).parameter(currency).value(voucherInstruction.getCurrency())
                    .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_CURRENCY)
                    .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedCurrencyLength, VoucherValidatorsEnum.INVALID_CURRENCY_LENGTH);

            // Check for amount
            validatorBuilder.reset().resource(resource).parameter(amount).value(voucherInstruction.getAmount())
                    .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_AMOUNT)
                    .validateBigDecimalFieldNotNegativeWithFailureCode(VoucherValidatorsEnum.INVALID_NEGATIVE_AMOUNT);

            // Check for payeeFunctionalID
            validatorBuilder.reset().resource(resource).parameter(payeeFunctionalID).value(voucherInstruction.getPayeeFunctionalID())
                    .isNullWithFailureCode(VoucherValidatorsEnum.INVALID_PAYEE_FUNCTIONAL_ID)
                    .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(expectedPayeeFunctionalIDLength, VoucherValidatorsEnum.INVALID_PAYEE_FUNCTIONAL_ID_LENGTH);

            // Check for narration
            validatorBuilder.reset().resource(resource).parameter(narration).value(voucherInstruction.getNarration()).ignoreIfNull()
                    .validateFieldMaxLengthWithFailureCode(maximumNarrationLength, VoucherValidatorsEnum.INVALID_NARRATION_LENGTH);
        });

        // If errors exist, build and return PhErrorDTO
        if (validatorBuilder.hasError()) {
            validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                    .errorCode(VoucherValidatorsEnum.VOUCHER_SCHEMA_VALIDATION_ERROR.getCode())
                    .errorDescription(VoucherValidatorsEnum.VOUCHER_SCHEMA_VALIDATION_ERROR.getMessage())
                    .developerMessage(VoucherValidatorsEnum.VOUCHER_SCHEMA_VALIDATION_ERROR.getMessage())
                    .defaultUserMessage(VoucherValidatorsEnum.VOUCHER_SCHEMA_VALIDATION_ERROR.getMessage());

            PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
            phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);
            return phErrorDTOBuilder.build();
        }

        return null;
    }
}
