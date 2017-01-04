package seedu.addressbook.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.tag.UniqueTagList;
import seedu.addressbook.util.TestUtil;
import seedu.addressbook.util.TypicalPersons;

import static seedu.addressbook.util.TestUtil.assertCommandResult;

public class ViewCommandTest {
    private AddressBook addressBook;
    private AddressBook emptyAddressBook;
    private List<ReadOnlyPerson> emptyDisplayList = new ArrayList<ReadOnlyPerson>();
    private List<ReadOnlyPerson> listWithAll;
    private List<ReadOnlyPerson> listWithSome;

    @Before
    public void setUp() throws Exception {
        emptyAddressBook = TestUtil.createAddressBook();

        TypicalPersons td = new TypicalPersons();
        addressBook = td.getTypicalAddressBook();
        listWithAll = Arrays.asList(td.getTypicalPersons());
        listWithSome = Arrays.asList(td.amy, td.candy, td.dan);
    }

    @Test
    public void viewCommand_invalidIndex_returnsInvalidIndexMessage() {
        // empty addressbook
        assertViewErrorInvalidIndex(emptyAddressBook, emptyDisplayList, 1);

        // non-empty addressbook
        assertViewErrorInvalidIndex(addressBook, listWithAll, -1);
        assertViewErrorInvalidIndex(addressBook, listWithAll, 0);
        assertViewErrorInvalidIndex(addressBook, listWithAll, listWithAll.size() + 1);
    }

    @Test
    public void viewCommand_personNotInAddressBook_returnsPersonNotInAddressBookMessage() throws Exception {
        // generate person not in addressbook, add to displayList
        ReadOnlyPerson someone = new Person(new Name("me"),
                                            new Phone("123", true),
                                            new Email("some@hey.go", true),
                                            new Address("nus", false),
                                            new UniqueTagList(Collections.emptySet()));
        listWithAll = new ArrayList<ReadOnlyPerson>(listWithAll);
        listWithAll.add(someone);

        // empty addressbook
        assertViewErrorPersonNotInAddressBook(emptyAddressBook, listWithAll, 1);

        // non-empty addressbook
        assertViewErrorPersonNotInAddressBook(addressBook, listWithAll, listWithAll.size());
    }

    @Test
    public void viewCommand_validIndex_returnsPersonDetails() {
        // person with no private information
        assertViewSuccess(addressBook, listWithAll, 1);

        // person with some private information
        assertViewSuccess(addressBook, listWithAll, 2);

        // person with all private information
        assertViewSuccess(addressBook, listWithAll, 4);

        // addressbook has more people than displayList
        // This can happen when view from filtered list caused by some commands(eg. FindCommand)
        assertViewSuccess(addressBook, listWithSome, 1);
    }

    private Command generateViewCommand(AddressBook addressBook, List<ReadOnlyPerson> displayList, int index) {
        ViewCommand command = new ViewCommand(index);
        command.setData(addressBook, displayList);

        return command;
    }

    private Command generateViewAllCommand(AddressBook addressBook, List<ReadOnlyPerson> displayList, int index) {
        Command command = new ViewAllCommand(index);
        command.setData(addressBook, displayList);

        return command;
    }

    /**
     * Asserts that the details of the person at specific index can be successfully retrieved
     * and displayed.
     */
    private void assertViewSuccess(AddressBook addressBook, List<ReadOnlyPerson> list, int index) {
        ReadOnlyPerson personToBeViewed = list.get(index - 1); // -1 is because index is one-indexed

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS,
                                               personToBeViewed.getAsTextHidePrivate());
        assertViewCommandBehaviour(addressBook, list, index, expectedMessage);

        expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_DETAILS,
                                        personToBeViewed.getAsTextShowAll());
        assertViewAllCommandBehaviour(addressBook, list, index, expectedMessage);
    }

    /**
     * Asserts that the details of person at specific index cannot be retrieved due to
     * invalid index.
     */
    private void assertViewErrorInvalidIndex(AddressBook addressBook, List<ReadOnlyPerson> list, int index) {
        assertCommandError(addressBook, list, index, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Asserts that the details of person at specific index cannot be retrieved due to
     * person not existing in the addressbook.
     */
    private void assertViewErrorPersonNotInAddressBook(AddressBook addressBook, List<ReadOnlyPerson> list, int index) {
        assertCommandError(addressBook, list, index, Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
    }

    /**
     * Asserts that the Viewcommand and ViewAllcommand reports error for given input
     */
    private void assertCommandError(AddressBook addressBook, List<ReadOnlyPerson> list, int index,
                                                                            String expectedMessage) {
        assertViewCommandBehaviour(addressBook, list, index, expectedMessage);
        assertViewAllCommandBehaviour(addressBook, list, index, expectedMessage);
    }

    /**
     * Asserts that the View command gives correct feedback information
     */
    private void assertViewCommandBehaviour(AddressBook addressBook, List<ReadOnlyPerson> list, int index, String expectedMessage) {
        Command command = generateViewCommand(addressBook, list, index);
        assertCommandResult(command, addressBook, TestUtil.clone(addressBook), expectedMessage);
    }

    /**
     * Asserts that the ViewAll command gives correct feedback information
     */
    private void assertViewAllCommandBehaviour(AddressBook addressBook, List<ReadOnlyPerson> list, int index, String expectedMessage) {
        Command command = generateViewAllCommand(addressBook, list, index);
        assertCommandResult(command, addressBook, TestUtil.clone(addressBook), expectedMessage);
    }
}
