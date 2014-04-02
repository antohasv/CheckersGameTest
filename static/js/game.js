var hostname = window.location.hostname;
var strokeWS = new WebSocket('ws://' + hostname + ':8052/ws/');
var chatWS = new WebSocket('ws://' + hostname + ':8012/ws/');
var mouse_down_handler;
var mouse_up_handler;
var fieldSize = 8;
var scrollSize = 0;

$(document).ready(function() {
    drawTable();
    $('#surrend').click(function() {
        check(0, 0, 0, 0, "lose");
    });
    $('#sendButton').click(function() {
        sendMessage();
    });
    $('#msg').keypress(function(event){
       if(event.which === 13){
            sendMessage();
       } 
    });
});

strokeWS.onopen = function(event) {
    var data = '{"';
    var cookie = document.cookie;
    cookie = cookie.split('; ');
    $.each(cookie, function(index, value) {
        value = value.split('=');
        if (index !== 0) {
            data += ',"';
        }
        data += value[0] + '":"' + value[1] + '"';
    });
    data += '}';
    strokeWS.send(data);
};

strokeWS.onmessage = function(event) {
    if (event.data === '1') {
        check(-1, -1, -1, -1, "");
        return;
    }
    var json = JSON.parse(event.data);
    var myColor = json.color;
    var status = json.status;
    if ((myColor === 'white') || (myColor === 'black')) {
        prepare(myColor);
        if (myColor === 'white') {
            $('.' + myColor + '-figure').bind('mousedown', mouse_down_handler);
            $('html').bind('mouseup', mouse_up_handler);
            $('.' + myColor + '-figure').css('cursor', 'pointer');
        }
    }
    if (myColor === 'w') {
        myColor = 'white';
    }
    if (myColor === 'b') {
        myColor = 'black';
    }
    if (json.status === 'win') {
        $('#statistic').html('You win!');
        $('.white-figure').unbind('mousedown');
        $('.black-figure').unbind('mousedown');
    }
    else if (json.status === 'lose') {
        $('#statistic').html('You lose!');
        $('.white-figure').unbind('mousedown');
        $('.black-figure').unbind('mousedown');
    }
    else if (json.status === 'snapshot') {
        prepare(myColor);
        var data = json.field;
        var king = json.king;
        var next = json.next;
        var i, j;
        if (next === 'b')
            next = 'black';
        else if (next === 'w')
            next = 'white';
        for (i = 0; i < fieldSize; i++) {
            for (j = 0; j < fieldSize; j++) {
                if (myColor === 'white')
                    var el = $('#game-field td').eq(fieldSize * (fieldSize - 1 - i) + j);
                else
                    var el = $('#game-field td').eq(fieldSize * i + fieldSize - 1 - j);
                $(el).removeClass('figure').removeClass('white-figure').removeClass('black-figure').removeClass('king');
                if (data[i][j] === 'white')
                    $(el).addClass('figure').addClass('white-figure');
                else if (data[i][j] === 'black')
                    $(el).addClass('figure').addClass('black-figure');
                if (king[i][j] === 'true')
                    $(el).addClass('king');
            }
        }
        if (myColor === next) {
            $('.' + myColor + '-figure').bind('mousedown', mouse_down_handler);
            $('html').bind('mouseup', mouse_up_handler);
            $('.' + myColor + '-figure').css('cursor', 'pointer');
        }
    }
    else if (!isNaN(json.to_x)) {
        var from_x = json.from_x;
        var from_y = json.from_y;
        var to_x = json.to_x;
        var to_y = json.to_y;
        var next = json.next;
        var status = json.status;
        if (next === 'b')
            next = 'black';
        else if (next === 'w')
            next = 'white';
        var el = $('#game-field td').eq(fieldSize * from_y + from_x);
        var color = '';
        if ($(el).hasClass('white-figure'))
            color = 'white-figure';
        else if ($(el).hasClass('black-figure'))
            color = 'black-figure';
        else
            color = '';
        var king = false;
        if ($(el).hasClass('king'))
            king = true;
        if (color !== '') {
            $(el).removeClass('figure').removeClass('black-figure').removeClass('white-figure');
            $(el).css('cursor', 'default');
            el = $('#game-field td').eq(fieldSize * to_y + to_x);
            $(el).addClass('figure').removeClass('black-figure').removeClass('white-figure').addClass(color);
            if (king)
                $(el).addClass('king');
            if (myColor === color) {
                $(el).css('cursor', 'pointer');
            }
        }
        if (status === 'true') {
            var on_x = (to_x - from_x) / Math.abs(to_x - from_x);
            var on_y = (to_y - from_y) / Math.abs(to_y - from_y);
            var i;
            for (i = 0; i < Math.abs(to_x - from_x); i++) {
                var temp_el = $('#game-field td').eq(fieldSize * (from_y + on_y * i) + (from_x + on_x * i));
                $(temp_el).removeClass('figure').removeClass('black-figure').removeClass('white-figure').removeClass('king');
            }
        }
        if (status === 'true') {
            el = $('#game-field td').eq(fieldSize * to_y + to_x);
            if (myColor === 'black') {
                if (((color === '') && (to_y === 0)) || ((color === 'white-figure') && (to_y === fieldSize - 1)))
                    $(el).addClass('king');
            }
            else {
                if (((color === 'black-figure') && (to_y === fieldSize - 1)) || ((color === '') && (to_y === 0)))
                    $(el).addClass('king');
            }
        }
        if (myColor === next) {
            $('html').unbind('mousedown');
            $('.' + myColor + '-figure').bind('mousedown', mouse_down_handler);
            $('html').bind('mouseup', mouse_up_handler);
            $('.' + myColor + '-figure').css('cursor', 'pointer');
        }
    }
};

strokeWS.onerror = function(event) {
    strokeWS.close();
};

chatWS.onopen = function(event) {
    var data = '{"';
    var cookie = document.cookie;
    cookie = cookie.split('; ');
    $.each(cookie, function(index, value) {
        value = value.split('=');
        if (index !== 0) {
            data += ',"';
        }
        data += value[0] + '":"' + value[1] + '"';
    });
    data += '}';
    chatWS.send(data);
};

chatWS.onmessage = function(event) {
    var json = JSON.parse(event.data);
    var sender = json.sender;
    var text = json.text;
    var tr = $('<tr>' + '<td>' + sender + '</td>' + '<td id="msgText">' + text + '</td>' + '</tr>');
    $('#chat').append(tr);
    scrollSize+=100;
    $('#chat').scrollTop(scrollSize);
};



function sendMessage() {
    var message = document.getElementById("msg").value;
    document.getElementById("msg").value = '';
    var data = '{"text":"' + message + '"';
    var cookie = document.cookie;
    cookie = cookie.split('; ');
    $.each(cookie, function(index, value) {
        value = value.split('=');
        data += ',"' + value[0] + '":"' + value[1] + '"';
    });
    data += '}';
    chatWS.send(data);
}

function drawTable() {
    var table = $('<table id="game-field"></table>');
    for (i = 0; i < fieldSize; i++) {
        var tr = $('<tr></tr>');
        for (j = 0; j < fieldSize; j++) {
            var td = $('<td></td>').addClass('cell');
            if ((j + i) % 2 === 0) {
                $(td).addClass('white');
            }
            else {
                $(td).addClass('black');
            }
            $(td).appendTo($(tr));
        }
        $(table).append(tr);
    }
    $('#content').append(table);
}

function prepare(color) {
    var other_color = '';
    (color === 'black') ? other_color = 'white' : other_color = 'black';
    color = color + '-figure';
    other_color = other_color + '-figure';
    for (i = 0; i < fieldSize; i++) {
        for (j = 0; j < fieldSize; j++) {
            if ((j + i) % 2 !== 0) {
                var el = $('#game-field td').eq(fieldSize * i + j);
                if (i < 3) {
                    $(el).addClass(other_color).addClass('figure');
                }
                if (i > 4) {
                    $(el).addClass(color).addClass('figure');
                }
            }
        }
    }

    //стартовая ячейка в координатах
    var from_x = 0, from_y = 0;
    //координаты таблицы
    var table_y = Math.ceil($('#game-field').offset().top), table_x = Math.ceil($('#game-field').offset().left);

    mouse_down_handler = function(event) {
        from_x = event.pageX;
        from_y = event.pageY;
        if ($(this).hasClass('white-figure')) {
            $('#dragable').removeClass().addClass('white-figure');
            $(this).removeClass('white-figure');
        }
        else if ($(this).hasClass('black-figure')) {
            $('#dragable').removeClass().addClass('black-figure');
            $(this).removeClass('black-figure');
        }
        if ($(this).hasClass('king')) {
            $('#dragable').addClass('king');
            $(this).removeClass('king');
        }
        $('#dragable').css('display', 'block').css('top', event.pageY - 25).css('left', event.pageX - 25);
        $(this).removeClass('figure').removeClass('black-figure').removeClass('white-figure').removeClass('king');
        $(this).css('cursor', 'default');
        $('html').bind('mousemove', function(event) {
            $('#dragable').css('display', 'block').css('top', event.pageY - 25).css('left', event.pageX - 25);
        });
    };

    mouse_up_handler = function(event) {
        //стартовая ячейка в полях
        var x_from_in_table = Math.ceil((from_x - table_x) / 50);
        var y_from_in_table = Math.ceil((from_y - table_y) / 50);
        //текущая ячейка в полях
        var x = Math.ceil((event.pageX - table_x) / 50);
        var y = Math.ceil((event.pageY - table_y) / 50);
        var flag = false;
        var eat = false;
        var color_figure = '';
        var other_color = '';
        //проверка вне ячеек
        if ((x < 1) || (y < 1) || (x > fieldSize) || (y > fieldSize)) {
            flag = true;
        }
        //проверка, что из пустой клетки
        if (from_x * from_y === 0) {
            flag = true;
        }
        //корректировка округления
        if ((event.pageX - table_x) % 50 !== 0) {
            x--;
        }
        if ((event.pageY - table_y) % 50 !== 0) {
            y--;
        }
        if ((from_x - table_x) % 50 !== 0) {
            x_from_in_table--;
        }
        if ((from_y - table_y) % 50 !== 0) {
            y_from_in_table--;
        }

        var el = $('#game-field td').eq(fieldSize * y + x);
        //проверка, если клетка не черная
        if (!($(el).hasClass('black')) || ($(el).hasClass('figure')) || (!$(el).hasClass('cell'))) {
            flag = true;
        }
        if ((x - x_from_in_table === 0) || (y - y_from_in_table === 0)) {
            flag = true;
        }
        if (!$('#dragable').hasClass('king')) {
            if (((x - x_from_in_table === 1) || (x - x_from_in_table === -1)) && (y - y_from_in_table === 1)) {
                flag = true;
            }
            //дальше 2 рядов нельзя
            if (((Math.abs(y - y_from_in_table)) > 2) || (Math.abs(x - x_from_in_table)) > 2) {
                flag = true;
            }
            //выбор цвета
            if ($('#dragable').hasClass('black-figure')) {
                color_figure = 'black-figure';
                other_color = 'white-figure';
                if (((y - y_from_in_table === 2) || (y - y_from_in_table === -2)) && (!(flag))) {
                    var temp_x = Math.round((x + x_from_in_table) / 2);
                    var temp_y = Math.round((y + y_from_in_table) / 2);
                    var temp_el = $('#game-field td').eq(fieldSize * temp_y + temp_x);
                    if (!($(temp_el).hasClass('white-figure'))) {
                        flag = true;
                    }
                    if (!flag) {
                        eat = true;
                    }
                }
            }
            else if ($('#dragable').hasClass('white-figure')) {
                color_figure = 'white-figure';
                other_color = 'black-figure';
                //назад на предыдущий ряд ходить нельзя
                if (((y - y_from_in_table === 2) || (y - y_from_in_table === -2)) && (!(flag))) {
                    var temp_x = Math.round((x + x_from_in_table) / 2);
                    var temp_y = Math.round((y + y_from_in_table) / 2);
                    var temp_el = $('#game-field td').eq(fieldSize * temp_y + temp_x);
                    if (!($(temp_el).hasClass('black-figure'))) {
                        flag = true;
                    }
                    if (!flag) {
                        eat = true;
                    }
                }
            }
            if (color_figure === '') {
                flag = true;
            }
            if ((!eat) && (!flag)) {
                //есть обязательно!
                var i, j, k;
                var temp_el1 = $('#game-field td').eq(fieldSize * y_from_in_table + x_from_in_table);
                $(temp_el1).addClass('figure');
                for (i = 0; i < fieldSize; i++) {
                    for (j = 0; j < fieldSize; j++) {
                        temp_el = $('#game-field td').eq(fieldSize * i + j);
                        if (($(temp_el).hasClass(color_figure)) || ((x_from_in_table === j) && (y_from_in_table === i))) {
                            if (!($(temp_el).hasClass('king'))) {
                                var eat_el = $('#game-field td').eq(fieldSize * (i - 1) + (j - 1));
                                var next_el = $('#game-field td').eq(fieldSize * (i - 2) + (j - 2));
                                if ((i > 1) && (j > 1) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                    $('#statistic').html(i + ' ' + j);
                                    flag = true;
                                }
                                eat_el = $('#game-field td').eq(fieldSize * (i - 1) + (j + 1));
                                next_el = $('#game-field td').eq(fieldSize * (i - 2) + (j + 2));
                                if ((i > 1) && (j < fieldSize - 2) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                    $('#statistic').html(2);
                                    flag = true;
                                }
                                eat_el = $('#game-field td').eq(fieldSize * (i + 1) + (j - 1));
                                next_el = $('#game-field td').eq(fieldSize * (i + 2) + (j - 2));
                                if ((i < fieldSize - 2) && (j > 1) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                    $('#statistic').html(3);
                                    flag = true;
                                }
                                eat_el = $('#game-field td').eq(fieldSize * (i + 1) + (j + 1));
                                next_el = $('#game-field td').eq(fieldSize * (i + 2) + (j + 2));
                                if ((i < fieldSize - 2) && (j < fieldSize - 2) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                    $('#statistic').html(4);
                                    flag = true;
                                }
                            }
                            else {
                                var left_up = false, left_down = false, right_up = false, right_down = false;
                                for (k = 1; k < fieldSize; k++) {
                                    if ((i - k <= 1) || (j - k <= 1))
                                        break;
                                    eat_el = $('#game-field td').eq(fieldSize * (i - k) + (j - k));
                                    next_el = $('#game-field td').eq(fieldSize * (i - k - 1) + (j - k - 1));
                                    if (($(eat_el).hasClass(other_color))) {
                                        if (!($(next_el).hasClass('figure'))) {
                                            $('#statistic').html(5);
                                            left_up = true;
                                        }
                                        else
                                            break;
                                    }
                                }
                                for (k = 1; k < fieldSize; k++) {
                                    if ((i + k >= fieldSize - 2) || (j + k >= fieldSize - 2))
                                        break;
                                    eat_el = $('#game-field td').eq(fieldSize * (i + k) + (j + k));
                                    next_el = $('#game-field td').eq(fieldSize * (i + k + 1) + (j + k + 1));
                                    if (($(eat_el).hasClass(other_color))) {
                                        if (!($(next_el).hasClass('figure'))) {
                                            $('#statistic').html(6);
                                            right_down = true;
                                        }
                                        else
                                            break;
                                    }
                                }
                                for (k = 1; k < fieldSize; k++) {
                                    if ((i - k <= 1) || (j + k >= fieldSize - 2))
                                        break;
                                    eat_el = $('#game-field td').eq(fieldSize * (i - k) + (j + k));
                                    next_el = $('#game-field td').eq(fieldSize * (i - k - 1) + (j + k + 1));
                                    if (($(eat_el).hasClass(other_color))) {
                                        if (!($(next_el).hasClass('figure'))) {
                                            $('#statistic').html(7);
                                            right_up = true;
                                        }
                                        else
                                            break;
                                    }
                                }
                                for (k = 1; k < fieldSize; k++) {
                                    if ((i + k >= fieldSize - 2) || (j - k <= 1))
                                        break;
                                    eat_el = $('#game-field td').eq(fieldSize * (i + k) + (j - k));
                                    next_el = $('#game-field td').eq(fieldSize * (i + k + 1) + (j - k - 1));
                                    if (($(eat_el).hasClass(other_color))) {
                                        if (!($(next_el).hasClass('figure'))) {
                                            $('#statistic').html(8);
                                            left_down = true;
                                        }
                                        else
                                            break;
                                    }
                                }
                                if (left_up || left_down || right_up || right_down) {
                                    flag = true;
                                }
                            }
                        }
                    }
                }
                $(temp_el1).removeClass('figure');
            }
            if (!(flag)) {
                //вроде валидный ход
                check(x_from_in_table, y_from_in_table, x, y, "");
                $(el).addClass('figure').removeClass('black-figure').removeClass('white-figure').addClass(color_figure).removeClass('king');
                $('.' + color_figure).css('cursor', 'default');
                $('.' + color_figure).unbind('mousedown');
            }
        }
        else {
            if (Math.abs(x_from_in_table - x) !== Math.abs(y_from_in_table - y))
                flag = true;
            var on_x = 0, on_y = 0;
            if (!flag) {
                on_x = (x - x_from_in_table) / Math.abs(x - x_from_in_table);
                on_y = (y - y_from_in_table) / Math.abs(y - y_from_in_table);
            }
            if ($('#dragable').hasClass('black-figure')) {
                color_figure = 'black-figure';
                other_color = 'white-figure';
            }
            else if ($('#dragable').hasClass('white-figure')) {
                color_figure = 'white-figure';
                other_color = 'black-figure';
            }
            else {
                flag = true;
            }
            if (!flag) {
                var kol = 0;
                for (i = 1; i < Math.abs(x - x_from_in_table); i++) {
                    var temp_el = $('#game-field td').eq(fieldSize * (y_from_in_table + i * on_y) + (x_from_in_table + i * on_x));
                    if ($(temp_el).hasClass(color_figure)) {
                        kol += 2;
                    }
                    else if ($(temp_el).hasClass(other_color)) {
                        kol += 1;
                    }
                }
                if (kol > 1)
                    flag = true;
                else if (kol === 1)
                    eat = true;
                if ((!eat) && (!flag)) {
                    //есть обязательно!
                    var i, j, k;
                    for (i = 0; i < fieldSize; i++) {
                        for (j = 0; j < fieldSize; j++) {
                            temp_el = $('#game-field td').eq(fieldSize * i + j);
                            if (($(temp_el).hasClass(color_figure)) || ((x_from_in_table === j) && (y_from_in_table === i))) {
                                if (!($(temp_el).hasClass('king'))) {
                                    var eat_el = $('#game-field td').eq(fieldSize * (i - 1) + (j - 1));
                                    var next_el = $('#game-field td').eq(fieldSize * (i - 2) + (j - 2));
                                    if ((i > 1) && (j > 1) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                        $('#statistic').html(9);
                                        flag = true;
                                    }
                                    eat_el = $('#game-field td').eq(fieldSize * (i - 1) + (j + 1));
                                    next_el = $('#game-field td').eq(fieldSize * (i - 2) + (j + 2));
                                    if ((i > 1) && (j < fieldSize - 2) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                        $('#statistic').html(10);
                                        flag = true;
                                    }
                                    eat_el = $('#game-field td').eq(fieldSize * (i + 1) + (j - 1));
                                    next_el = $('#game-field td').eq(fieldSize * (i + 2) + (j - 2));
                                    if ((i < fieldSize - 2) && (j > 1) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                        $('#statistic').html(11);
                                        flag = true;
                                    }
                                    eat_el = $('#game-field td').eq(fieldSize * (i + 1) + (j + 1));
                                    next_el = $('#game-field td').eq(fieldSize * (i + 2) + (j + 2));
                                    if ((i < fieldSize - 2) && (j < fieldSize - 2) && ($(eat_el).hasClass(other_color)) && (!($(next_el).hasClass('figure')))) {
                                        $('#statistic').html(12);
                                        flag = true;
                                    }
                                }
                                else {
                                    var left_up = false, left_down = false, right_up = false, right_down = false;
                                    for (k = 1; k < fieldSize; k++) {
                                        if ((i - k <= 1) || (j - k <= 1))
                                            break;
                                        eat_el = $('#game-field td').eq(fieldSize * (i - k) + (j - k));
                                        next_el = $('#game-field td').eq(fieldSize * (i - k - 1) + (j - k - 1));
                                        if (($(eat_el).hasClass(other_color))) {
                                            if (!($(next_el).hasClass('figure'))) {
                                                $('#statistic').html(13);
                                                left_up = true;
                                            }
                                            else
                                                break;
                                        }
                                    }
                                    for (k = 1; k < fieldSize; k++) {
                                        if ((i + k >= fieldSize - 2) || (j + k >= fieldSize - 2))
                                            break;
                                        eat_el = $('#game-field td').eq(fieldSize * (i + k) + (j + k));
                                        next_el = $('#game-field td').eq(fieldSize * (i + k + 1) + (j + k + 1));
                                        if (($(eat_el).hasClass(other_color))) {
                                            if (!($(next_el).hasClass('figure'))) {
                                                $('#statistic').html(14);
                                                right_down = true;
                                            }
                                            else
                                                break;
                                        }
                                    }
                                    for (k = 1; k < fieldSize; k++) {
                                        if ((i - k <= 1) || (j + k >= fieldSize - 2))
                                            break;
                                        eat_el = $('#game-field td').eq(fieldSize * (i - k) + (j + k));
                                        next_el = $('#game-field td').eq(fieldSize * (i - k - 1) + (j + k + 1));
                                        if (($(eat_el).hasClass(other_color))) {
                                            if (!($(next_el).hasClass('figure'))) {
                                                $('#statistic').html(15);
                                                right_up = true;
                                            }
                                            else
                                                break;
                                        }
                                    }
                                    for (k = 1; k < fieldSize; k++) {
                                        if ((i + k >= fieldSize - 2) || (j - k <= 1))
                                            break;
                                        eat_el = $('#game-field td').eq(fieldSize * (i + k) + (j - k));
                                        next_el = $('#game-field td').eq(fieldSize * (i + k + 1) + (j - k - 1));
                                        if (($(eat_el).hasClass(other_color))) {
                                            if (!($(next_el).hasClass('figure'))) {
                                                $('#statistic').html(16);
                                                left_down = true;
                                            }
                                            else
                                                break;
                                        }
                                    }
                                    if (left_up || left_down || right_up || right_down) {
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!flag) {
                    check(x_from_in_table, y_from_in_table, x, y, "");
                    $(el).addClass('figure').removeClass('black-figure').removeClass('white-figure').addClass(color_figure).addClass('king');
                    $('.' + color_figure).css('cursor', 'default');
                    $('.' + color_figure).unbind('mousedown');
                }
            }

        }
        if ((color_figure !== '') && (flag)) {
            x = Math.ceil((from_x - table_x) / 50);
            y = Math.ceil((from_y - table_y) / 50);
            if ((from_x - table_x) % 50 !== 0) {
                x--;
            }
            if ((from_y - table_y) % 50 !== 0) {
                y--;
            }
            el = $('#game-field td').eq(fieldSize * y + x);
            $(el).addClass('figure').removeClass('black-figure').removeClass('white-figure').addClass(color_figure).removeClass('king');
            if ($('#dragable').hasClass('king'))
                $(el).addClass('king');
            $(el).css('cursor', 'pointer');
        }
        $('#dragable').css('display', 'none').removeClass();
        $('#dragable').css('top', -1000).css('left', -1000);
        $('html').unbind('mousemove');
    }
    ;
}

function check(x_from, y_from, x, y, status) {
    var data = '{"from_x":' + x_from + ',"from_y":' + y_from + ',"to_x":' + x + ',"to_y":' + y + ',"status":"' + status + '"';
    var cookie = document.cookie;
    cookie = cookie.split('; ');
    $.each(cookie, function(index, value) {
        value = value.split('=');
        data += ',"' + value[0] + '":"' + value[1] + '"';
    });
    data += '}';
    strokeWS.send(data);
}