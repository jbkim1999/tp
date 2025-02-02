package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FASTFOOD;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_TECH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_APPLE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BURGER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FASTFOOD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_TECH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CLIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CLIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_CLIENT;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditClientDescriptor;
import seedu.address.model.client.Address;
import seedu.address.model.client.Email;
import seedu.address.model.client.Name;
import seedu.address.model.client.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditClientDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_APPLE, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_APPLE, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_APPLE, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_APPLE, Phone.MESSAGE_CONSTRAINTS);

        // valid phone followed by invalid phone. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        assertParseFailure(parser, "1" + PHONE_DESC_BURGER + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Client} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FASTFOOD + TAG_DESC_TECH + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FASTFOOD + TAG_EMPTY + TAG_DESC_TECH, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FASTFOOD + TAG_DESC_TECH, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC
                        + VALID_ADDRESS_APPLE
                        + VALID_PHONE_APPLE,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_CLIENT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BURGER + TAG_DESC_TECH
                + EMAIL_DESC_APPLE + ADDRESS_DESC_APPLE + NAME_DESC_APPLE + TAG_DESC_FASTFOOD;

        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withName(VALID_NAME_APPLE)
                .withPhone(VALID_PHONE_BURGER).withEmail(VALID_EMAIL_APPLE).withAddress(VALID_ADDRESS_APPLE)
                .withTags(VALID_TAG_TECH, VALID_TAG_FASTFOOD).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_CLIENT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BURGER + EMAIL_DESC_APPLE;

        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withPhone(VALID_PHONE_BURGER)
                .withEmail(VALID_EMAIL_APPLE).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_CLIENT;
        String userInput = targetIndex.getOneBased() + NAME_DESC_APPLE;
        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withName(VALID_NAME_APPLE).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_APPLE;
        descriptor = new EditClientDescriptorBuilder().withPhone(VALID_PHONE_APPLE).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_APPLE;
        descriptor = new EditClientDescriptorBuilder().withEmail(VALID_EMAIL_APPLE).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_APPLE;
        descriptor = new EditClientDescriptorBuilder().withAddress(VALID_ADDRESS_APPLE).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FASTFOOD;
        descriptor = new EditClientDescriptorBuilder().withTags(VALID_TAG_FASTFOOD).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_CLIENT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_APPLE
                + ADDRESS_DESC_APPLE + EMAIL_DESC_APPLE
                + TAG_DESC_FASTFOOD + PHONE_DESC_APPLE
                + ADDRESS_DESC_APPLE + EMAIL_DESC_APPLE
                + TAG_DESC_FASTFOOD
                + PHONE_DESC_BURGER + ADDRESS_DESC_BURGER
                + EMAIL_DESC_BURGER + TAG_DESC_TECH;

        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withPhone(VALID_PHONE_BURGER)
                .withEmail(VALID_EMAIL_BURGER).withAddress(VALID_ADDRESS_BURGER)
                .withTags(VALID_TAG_FASTFOOD, VALID_TAG_TECH)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_CLIENT;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BURGER;
        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withPhone(VALID_PHONE_BURGER).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + EMAIL_DESC_BURGER + INVALID_PHONE_DESC + ADDRESS_DESC_BURGER
                + PHONE_DESC_BURGER;
        descriptor = new EditClientDescriptorBuilder().withPhone(VALID_PHONE_BURGER).withEmail(VALID_EMAIL_BURGER)
                .withAddress(VALID_ADDRESS_BURGER).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_CLIENT;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditClientDescriptor descriptor = new EditClientDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
