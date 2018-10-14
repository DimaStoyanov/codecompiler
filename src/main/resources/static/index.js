var languageSelector = $('#language');
var codeSelector = $('#code');
var compileResponseSelector = $('#compile-response');
var compilePB = $('#compile-pb');

var submissionSelector = $('#submission-id');
var inputSelector = $('#stdin');
var tlSelector = $('#tl');
var mlSelector = $('#ml');
var runResponseSelector = $('#run-response');
var runPB = $('#run-pb');


$('#compile').click(function () {
    compilePB.show();
    compileResponseSelector.text('');
    var payload = {
        language: languageSelector.val(),
        sourceCode: codeSelector.val()
    };
    $.ajax({
        url: '/compile-submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        compilePB.hide();
        compileResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        compilePB.hide();
        compileResponseSelector.text(prettyJSON(resp.responseJSON));
    })
});

$('#run').click(function () {
    runPB.show();
    runResponseSelector.text('');
    var payload = {
        submissionId: submissionSelector.val(),
        input: inputSelector.val(),
        memoryLimit: mlSelector.val(),
        executionTimeLimit: tlSelector.val()
    };
    $.ajax({
        url: '/submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        runPB.hide();
        runResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        runPB.hide();
        runResponseSelector.text(prettyJSON(resp.responseJSON));
    })
});


function prettyJSON(data) {
    return JSON.stringify(data, null, 2);
}
